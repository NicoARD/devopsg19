package com.napier.sem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration and connection management using HikariCP
 */
public class DatabaseConfig {
    
    private static HikariDataSource dataSource;
    
    /**
     * Initialize the database connection pool
     */
    public static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Database connection properties - can be overridden by environment variables
        String host = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
        String port = System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3307";
        String database = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "world";
        String username = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "devuser";
        String password = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "devpass";
        
        // JDBC URL
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, database);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // HikariCP config setup
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        
        // Connection pool name for monitoring
        config.setPoolName("MySQL-Pool");
        
        dataSource = new HikariDataSource(config);
        
        System.out.println("Database connection pool initialized");
        System.out.println("JDBC URL: " + jdbcUrl);
        System.out.println("Username: " + username);
    }
    
    /**
     * Get a connection from the pool
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource.getConnection();
    }
    
    /**
     * Close the data source when application shuts down
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }
    
    /**
     * Get the data source instance
     * @return HikariDataSource instance
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource;
    }
}