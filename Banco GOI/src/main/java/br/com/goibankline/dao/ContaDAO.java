package br.com.goibankline.dao;

import br.com.goibankline.model.Conta;
import br.com.goibankline.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Conta – inclui logs para depuração.
 */
public class ContaDAO {

    /* ===============================================================
       INSERT
       =============================================================== */
    public void inserir(Conta conta) {
        String sql = "INSERT INTO Conta (ID_Cliente, Numero_Conta, Senha, " +
                "Saldo, Limite_Credito, Data_Criacao) VALUES (?,?,?,?,?,?)";
        System.out.println("[DAO] inserir()  ID_Cliente=" + conta.getCliente().getId());

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt       (1, conta.getCliente().getId());
            st.setString    (2, conta.getNumeroConta());
            st.setString    (3, conta.getSenha());
            st.setBigDecimal(4, conta.getSaldo());
            st.setBigDecimal(5, conta.getLimiteCredito());
            st.setDate      (6, Date.valueOf(conta.getDataCriacao()));

            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    conta.setIdConta(rs.getInt(1));
                    System.out.println("[DAO] inserir() gerou IdConta=" + conta.getIdConta());
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ===============================================================
       UPDATE
       =============================================================== */
    public void atualizar(Conta conta) {
        System.out.println("[DAO] atualizar()  IdConta=" + conta.getIdConta());
        String sql = "UPDATE Conta SET ID_Cliente=?, Numero_Conta=?, Senha=?, " +
                "Saldo=?, Limite_Credito=?, Data_Criacao=? WHERE IdConta=?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt       (1, conta.getCliente().getId());
            st.setString    (2, conta.getNumeroConta());
            st.setString    (3, conta.getSenha());
            st.setBigDecimal(4, conta.getSaldo());
            st.setBigDecimal(5, conta.getLimiteCredito());
            st.setDate      (6, Date.valueOf(conta.getDataCriacao()));
            st.setInt       (7, conta.getIdConta());

            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ===============================================================
       DELETE
       =============================================================== */
    public void excluir(int idConta) {
        System.out.println("[DAO] excluir()  IdConta=" + idConta);
        String sql = "DELETE FROM Conta WHERE IdConta=?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, idConta);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*------------------------------------------------------------------
      Busca pela FOREIGN-KEY ID_Cliente  (usar nos servlets)
      ------------------------------------------------------------------*/
    public Conta buscarPorClienteId(int idCliente) {
        System.out.println("[DAO] buscarPorClienteId()  idCliente=" + idCliente);
        String sql = "SELECT * FROM Conta WHERE ID_Cliente = ?";
        Conta conta = null;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    conta = montarConta(rs);
                    System.out.println("[DAO] > Conta encontrada  IdConta=" +
                            conta.getIdConta() + "  Numero=" + conta.getNumeroConta());
                } else {
                    System.out.println("[DAO] > Nenhuma conta para esse ID_Cliente");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return conta;        // null se não achou
    }

    /* ===============================================================
       BUSCAR por IdConta  (opcional para outros fluxos)
       =============================================================== */
    public Conta buscarPorId(int idConta) {
        System.out.println("[DAO] buscarPorId()  IdConta=" + idConta);
        String sql = "SELECT * FROM Conta WHERE IdConta = ?";
        Conta conta = null;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, idConta);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    conta = montarConta(rs);
                    System.out.println("[DAO] > Conta encontrada");
                } else {
                    System.out.println("[DAO] > Nenhuma conta com esse IdConta");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return conta;
    }

    /* ===============================================================
       LISTAR TODOS (apenas para debug/admin)
       =============================================================== */
    public List<Conta> listarTodos() {
        System.out.println("[DAO] listarTodos()");
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT * FROM Conta";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) { lista.add(montarConta(rs)); }
            System.out.println("[DAO] > retornou " + lista.size() + " contas");
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /* ---------------------------------------------------------------
       Helper
       --------------------------------------------------------------- */
    private Conta montarConta(ResultSet rs) throws SQLException {
        Conta c = new Conta();
        c.setIdConta(rs.getInt("IdConta"));

        Cliente cli = new Cliente();
        cli.setId(rs.getInt("ID_Cliente"));
        c.setCliente(cli);

        c.setNumeroConta   (rs.getString("Numero_Conta"));
        c.setSenha         (rs.getString("Senha"));
        c.setSaldo         (rs.getBigDecimal("Saldo"));
        c.setLimiteCredito (rs.getBigDecimal("Limite_Credito"));
        c.setDataCriacao   (rs.getDate("Data_Criacao").toLocalDate());
        return c;
    }
}
