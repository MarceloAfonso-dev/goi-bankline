package br.com.goibankline.dao;

import br.com.goibankline.model.Conta;
import br.com.goibankline.model.Cliente;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferenciaDAO {

    // Busca a Conta associada a um CPF informado.
    private Conta buscarContaPorCPF(Connection con, String cpf) throws SQLException {
        String sql = "SELECT c.IdConta, c.ID_Cliente, c.Numero_Conta, c.Senha, c.Saldo, c.Limite_Credito, c.Data_Criacao " +
                "FROM Conta c INNER JOIN Cliente cli ON c.ID_Cliente = cli.ID " +
                "WHERE cli.CPF = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conta conta = new Conta();
                    conta.setIdConta(rs.getInt("IdConta"));

                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("ID_Cliente"));
                    conta.setCliente(cliente);

                    conta.setNumeroConta(rs.getString("Numero_Conta"));
                    conta.setSenha(rs.getString("Senha"));
                    conta.setSaldo(rs.getBigDecimal("Saldo"));
                    conta.setLimiteCredito(rs.getBigDecimal("Limite_Credito"));
                    if (rs.getDate("Data_Criacao") != null) {
                        conta.setDataCriacao(rs.getDate("Data_Criacao").toLocalDate());
                    }
                    return conta;
                }
            }
        }
        return null;
    }

    // Realiza a transferência entre as contas associadas aos CPFs informados.
    public boolean transferir(String cpfOrigem, String cpfDestino, BigDecimal valor) {
        Connection con = null;
        try {
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            Conta contaOrigem = buscarContaPorCPF(con, cpfOrigem);
            Conta contaDestino = buscarContaPorCPF(con, cpfDestino);

            if (contaOrigem == null || contaDestino == null) {
                con.rollback();
                return false;
            }

            // Verifica se a conta de origem possui saldo suficiente.
            if (contaOrigem.getSaldo().compareTo(valor) < 0) {
                con.rollback();
                return false;
            }

            BigDecimal novoSaldoOrigem = contaOrigem.getSaldo().subtract(valor);
            BigDecimal novoSaldoDestino = contaDestino.getSaldo().add(valor);

            String sql = "UPDATE Conta SET Saldo = ? WHERE IdConta = ?";
            try (PreparedStatement stmtOrigem = con.prepareStatement(sql);
                 PreparedStatement stmtDestino = con.prepareStatement(sql)) {

                stmtOrigem.setBigDecimal(1, novoSaldoOrigem);
                stmtOrigem.setInt(2, contaOrigem.getIdConta());
                stmtOrigem.executeUpdate();

                stmtDestino.setBigDecimal(1, novoSaldoDestino);
                stmtDestino.setInt(2, contaDestino.getIdConta());
                stmtDestino.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}