package br.com.goibankline.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    // Lista para armazenar todas as conexões abertas
    private static List<Connection> connectionPool = new ArrayList<>();

    // Constantes para a URL, usuário e senha do banco de dados RDS
    private static final String URL = "jdbc:mysql://goi-database.cjsxgdipkurd.us-east-1.rds.amazonaws.com:3306/goi-database";
    private static final String URL_NO_DB = "jdbc:mysql://goi-database.cjsxgdipkurd.us-east-1.rds.amazonaws.com:3306/";
    private static final String USER = "admin";
    private static final String PASSWORD = "SENAC2005$";

    static {
        try {
            // Registrar o driver JDBC do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        }
    }

    // Método para obter uma conexão e adicionar à lista
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connectionPool.add(connection);
        LOGGER.info("Connection established and added to the pool.");
        return connection;
    }

    // Método para obter uma conexão sem especificar o banco de dados
    public static Connection getConnectionWithoutDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URL_NO_DB, USER, PASSWORD);
        connectionPool.add(connection);
        LOGGER.info("Connection without database established and added to the pool.");
        return connection;
    }

    // Método para fechar todas as conexões abertas
    public static void closeAllConnections() throws SQLException {
        for (Connection connection : connectionPool) {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Connection closed.");
            }
        }
        connectionPool.clear(); // Limpa a lista de conexões após fechá-las
        LOGGER.info("Connection pool cleared.");
    }
}