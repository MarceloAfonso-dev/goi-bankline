// ContaDAO.java
package br.com.goibankline.dao;

import br.com.goibankline.model.Conta;
import br.com.goibankline.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    public void inserir(Conta conta) {
        String sql = "INSERT INTO Conta (ID_Cliente, Numero_Conta, Senha, Saldo, Limite_Credito, Data_Criacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, conta.getCliente().getId());
            stmt.setString(2, conta.getNumeroConta());
            stmt.setString(3, conta.getSenha());
            stmt.setBigDecimal(4, conta.getSaldo());
            stmt.setBigDecimal(5, conta.getLimiteCredito());
            stmt.setDate(6, Date.valueOf(conta.getDataCriacao()));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    conta.setIdConta(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Conta conta) {
        String sql = "UPDATE Conta SET ID_Cliente = ?, Numero_Conta = ?, Senha = ?, Saldo = ?, Limite_Credito = ?, Data_Criacao = ? WHERE IdConta = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, conta.getCliente().getId());
            stmt.setString(2, conta.getNumeroConta());
            stmt.setString(3, conta.getSenha());
            stmt.setBigDecimal(4, conta.getSaldo());
            stmt.setBigDecimal(5, conta.getLimiteCredito());
            stmt.setDate(6, Date.valueOf(conta.getDataCriacao()));
            stmt.setInt(7, conta.getIdConta());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int idConta) {
        String sql = "DELETE FROM Conta WHERE IdConta = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------- somente o novo método ------------------
    public Conta buscarPorClienteId(int idCliente) {
        String sql = "SELECT * FROM Conta WHERE ID_Cliente = ?";
        Conta conta = null;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);          // ← usa ID_Cliente
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    conta = new Conta();
                    conta.setIdConta(rs.getInt("IdConta"));

                    Cliente cli = new Cliente();
                    cli.setId(idCliente);
                    conta.setCliente(cli);

                    conta.setNumeroConta(rs.getString("Numero_Conta"));
                    conta.setSenha(rs.getString("Senha"));
                    conta.setSaldo(rs.getBigDecimal("Saldo"));
                    conta.setLimiteCredito(rs.getBigDecimal("Limite_Credito"));
                    conta.setDataCriacao(rs.getDate("Data_Criacao").toLocalDate());
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return conta;     // null se não achou
    }


    public List<Conta> listarTodos() {
        String sql = "SELECT * FROM Conta";
        List<Conta> contas = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Conta conta = new Conta();
                conta.setIdConta(rs.getInt("IdConta"));

                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("ID_Cliente"));
                conta.setCliente(cliente);

                conta.setNumeroConta(rs.getString("Numero_Conta"));
                conta.setSenha(rs.getString("Senha"));
                conta.setSaldo(rs.getBigDecimal("Saldo"));
                conta.setLimiteCredito(rs.getBigDecimal("Limite_Credito"));
                conta.setDataCriacao(rs.getDate("Data_Criacao").toLocalDate());
                contas.add(conta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }
}


