package br.com.goibankline.dao;

import java.sql.*;

import java.sql.*;

public class CpfLiberadoDAO {

    private final Connection conn;

    public CpfLiberadoDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean cpfTemNomeLimpo(String cpf) {
        String sql = "SELECT 1 FROM cpfs_liberados WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}