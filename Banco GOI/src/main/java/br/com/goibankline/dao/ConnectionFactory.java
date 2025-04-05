package br.com.goibankline.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    private static List<Connection> connectionPool = new ArrayList<>();
    private static String URL;
    private static String URL_NO_DB;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = new Properties();
            // Caminho relativo ou absoluto do arquivo db.properties
            FileInputStream fis = new FileInputStream("db.properties");
            props.load(fis);

            URL = props.getProperty("db.url");
            URL_NO_DB = URL.substring(0, URL.lastIndexOf("/") + 1);
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load database properties file", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connectionPool.add(connection);
        LOGGER.info("Connection established and added to the pool.");
        return connection;
    }

    public static Connection getConnectionWithoutDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URL_NO_DB, USER, PASSWORD);
        connectionPool.add(connection);
        LOGGER.info("Connection without database established and added to the pool.");
        return connection;
    }

    public static void closeAllConnections() throws SQLException {
        for (Connection connection : connectionPool) {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Connection closed.");
            }
        }
        connectionPool.clear();
        LOGGER.info("Connection pool cleared.");
    }
}
