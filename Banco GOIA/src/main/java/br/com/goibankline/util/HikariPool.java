package br.com.goibankline.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariPool {

    private static final HikariDataSource ds;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(System.getenv("JDBC_URL"));            // ou leia do db.properties
        cfg.setUsername(System.getenv("JDBC_USER"));
        cfg.setPassword(System.getenv("JDBC_PASS"));

        /* ajustes principais */
        cfg.setMaximumPoolSize(15);   // ~10-20 para 30 usuários
        cfg.setMinimumIdle(3);
        cfg.setConnectionTimeout(10000);   // 10 s
        cfg.setIdleTimeout(300000);        // 5 min
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        ds = new HikariDataSource(cfg);
    }

    private HikariPool() {}            // nenhum new

    public static DataSource getDataSource() {
        return ds;
    }
}
