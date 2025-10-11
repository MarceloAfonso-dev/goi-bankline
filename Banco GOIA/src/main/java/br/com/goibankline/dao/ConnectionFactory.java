package br.com.goibankline.dao;

import br.com.goibankline.util.PropertiesLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger LOG = Logger.getLogger(ConnectionFactory.class.getName());
    private static final HikariDataSource ds;

    /*  pool criado uma única vez */
    static {
        Properties p = PropertiesLoader.load("db.properties");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl (p.getProperty("db.url"));
        cfg.setUsername(p.getProperty("db.user"));
        cfg.setPassword(p.getProperty("db.password"));
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        /* --------- ajustes principais --------- */
        cfg.setMaximumPoolSize(15);     // (~10-20 para ±30 usuários)
        cfg.setMinimumIdle(3);
        cfg.setConnectionTimeout(10000);   // 10 s
        cfg.setIdleTimeout(300000);        // 5 min

        ds = new HikariDataSource(cfg);
        LOG.info("HikariCP inicializado com maxPoolSize=" + cfg.getMaximumPoolSize());
    }

    private ConnectionFactory() {}  // utilitário

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();   // vem do pool; rápido!
    }
}
