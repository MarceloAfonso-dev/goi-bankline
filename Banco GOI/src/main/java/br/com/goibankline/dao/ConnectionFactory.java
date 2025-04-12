package br.com.goibankline.dao;

import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.*;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = new Properties();
            InputStream is = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                LOGGER.severe("Arquivo db.properties não encontrado na classpath.");
            } else {
                props.load(is);
            }
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver não encontrado", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Não foi possível carregar o arquivo de propriedades do banco", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        LOGGER.info("Conexão estabelecida.");
        return connection;
    }
}
