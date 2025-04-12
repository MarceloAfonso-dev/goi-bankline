package br.com.goibankline.dao;

import br.com.goibankline.model.Cliente;
import java.sql.*;

public class ClienteDAO {

    public Cliente buscarPorCPF(String cpf) {
        Cliente cliente = null;
        String sql = "SELECT c.ID, c.Nome, c.Sobrenome, c.CPF, c.Telefone, c.CEP, c.Data_Nascimento, co.Senha " +
                "FROM Cliente c LEFT JOIN Conta co ON c.ID = co.ID_Cliente " +
                "WHERE c.CPF = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("ID"));
                    cliente.setNome(rs.getString("Nome"));
                    cliente.setSobrenome(rs.getString("Sobrenome"));
                    cliente.setCpf(rs.getString("CPF"));
                    cliente.setTelefone(rs.getString("Telefone"));
                    cliente.setCep(rs.getString("CEP"));
                    cliente.setDataNascimento(rs.getDate("Data_Nascimento").toLocalDate());
                    cliente.setSenha(rs.getString("Senha")); // Senha da conta
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }
}
