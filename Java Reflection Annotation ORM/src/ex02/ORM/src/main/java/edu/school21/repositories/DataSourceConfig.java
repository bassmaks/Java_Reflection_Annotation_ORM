package edu.school21.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DataSourceConfig {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("");
        dataSource = new HikariDataSource(config);
    }

    public static HikariDataSource getInstance() {
        return dataSource;
    }
}
