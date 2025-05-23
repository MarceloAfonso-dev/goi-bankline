package br.com.goibankline.dao;

import br.com.goibankline.model.Cliente;

import java.sql.*;

public class ClienteDAO {

    /* ============================================================
       BUSCAR POR CPF
       ============================================================ */
    public Cliente buscarPorCPF(String cpf) {
        Cliente cliente = null;
        String sql = "SELECT c.ID, c.Nome, c.Sobrenome, c.CPF, c.Telefone, c.CEP, " +
                "c.Data_Nascimento, co.Senha " +
                "FROM Cliente c " +
                "LEFT JOIN Conta co ON c.ID = co.ID_Cliente " +
                "WHERE c.CPF = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId            (rs.getInt   ("ID"));
                    cliente.setNome          (rs.getString("Nome"));
                    cliente.setSobrenome     (rs.getString("Sobrenome"));
                    cliente.setCpf           (rs.getString("CPF"));
                    cliente.setTelefone      (rs.getString("Telefone"));
                    cliente.setCep           (rs.getString("CEP"));
                    cliente.setDataNascimento(rs.getDate  ("Data_Nascimento").toLocalDate());
                    cliente.setSenha         (rs.getString("Senha")); // senha da conta
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cliente;
    }

    /* ============================================================
       INSERT – GRAVA, MAS NÃO DEVOLVE O ID (LEGADO)
       ============================================================ */
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Cliente " +
                     "(Nome, Sobrenome, CPF, Data_Nascimento, " +
                     "Email, Telefone, CEP) " +
                     "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, cliente.getNome());
            st.setString(2, cliente.getSobrenome());
            st.setString(3, cliente.getCpf());
            st.setDate  (4, Date.valueOf(cliente.getDataNascimento()));
            st.setString(5, cliente.getEmail());
            st.setString(6, cliente.getCelular());
            st.setString(7, cliente.getCep());
            st.executeUpdate();
        }
    }

    /* ============================================================
       INSERT – GRAVA E DEVOLVE A CHAVE GERADA
       ============================================================ */
    public int salvarRetornandoId(Cliente cliente) throws SQLException {
                String sql = "INSERT INTO Cliente " +
                             "(Nome, Sobrenome, CPF, Data_Nascimento, " +
                             "Email, Telefone, CEP) " +
                             "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, cliente.getNome());
            st.setString(2, cliente.getSobrenome());
            st.setString(3, cliente.getCpf());
            st.setDate  (4, Date.valueOf(cliente.getDataNascimento()));
            st.setString(5, cliente.getEmail());
            st.setString(6, cliente.getCelular());
            st.setString(7, cliente.getCep());

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    cliente.setId(idGerado);      // já preenche o objeto, se quiser
                    return idGerado;
                }
            }
        }
        return 0; // ou lance exceção se preferir
    }
}
