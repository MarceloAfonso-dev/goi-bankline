package br.com.goibankline.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionDAO {

    public static void main(String[] args) {
        try {
            // Tentando obter uma conexão
            Connection connection = ConnectionFactory.getConnection();
            if (connection != null) {
                System.out.println("Conexão estabelecida com sucesso!");
                connection.close(); // Fechando a conexão após o teste
            } else {
                System.out.println("Falha ao estabelecer a conexão.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer a conexão: " + e.getMessage());
        }
    }
}