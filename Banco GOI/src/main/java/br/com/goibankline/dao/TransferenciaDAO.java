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

        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);

            Conta contaOrigem  = buscarContaPorCPF(con, cpfOrigem);
            Conta contaDestino = buscarContaPorCPF(con, cpfDestino);

            if (contaOrigem == null || contaDestino == null) { con.rollback(); return false; }
            if (contaOrigem.getSaldo().compareTo(valor) < 0)   { con.rollback(); return false; }

            /* --------- 1) atualiza saldos --------- */
            String upd = "UPDATE Conta SET Saldo = ? WHERE IdConta = ?";
            try (PreparedStatement st = con.prepareStatement(upd)) {
                st.setBigDecimal(1, contaOrigem.getSaldo().subtract(valor));
                st.setInt       (2, contaOrigem.getIdConta());
                st.executeUpdate();

                st.setBigDecimal(1, contaDestino.getSaldo().add(valor));
                st.setInt       (2, contaDestino.getIdConta());
                st.executeUpdate();
            }

            /* --------- 2) grava os DOIS lançamentos --------- */
            String ins =
                    "INSERT INTO Transferencia " +
                            "(Id_Conta_Origem, Id_Conta_Destino, Valor, Data_Transferencia, " +
                            " Tipo_Transferencia, Status) " +
                            "VALUES (?,?,?,?,?,?)";

            java.sql.Date hoje = new java.sql.Date(System.currentTimeMillis());

            try (PreparedStatement st = con.prepareStatement(ins)) {
                /* lançamento NEGATIVO na origem */
                st.setInt       (1, contaOrigem.getIdConta());
                st.setInt       (2, contaDestino.getIdConta());
                st.setBigDecimal(3, valor.negate());               // -X
                st.setDate      (4, hoje);
                st.setString    (5, "dinheiro");
                st.setString    (6, "concluído");
                st.executeUpdate();

                /* lançamento POSITIVO na destinação */
                st.setInt       (1, contaOrigem.getIdConta());
                st.setInt       (2, contaDestino.getIdConta());
                st.setBigDecimal(3, valor);                        // +X
                st.setDate      (4, hoje);
                st.setString    (5, "dinheiro");
                st.setString    (6, "concluído");
                st.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}