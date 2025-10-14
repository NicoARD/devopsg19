package com.napier.sem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that run only in CI environment with MySQL available
 */
class DatabaseIntegrationTest {

    @BeforeEach
    void setUp() {
        // Set up database connection for CI environment
        System.setProperty("database.host", "127.0.0.1");
        System.setProperty("database.port", "3306");
        System.setProperty("database.name", "world");
        System.setProperty("database.user", "root");
        System.setProperty("database.password", "example");
    }

    @Test
    @DisplayName("Should connect to database in CI environment")
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void testDatabaseConnectionInCI() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(5), "Connection should be valid within 5 seconds");
            
            // Verify database name
            String url = connection.getMetaData().getURL();
            assertTrue(url.contains("world"), "Should be connected to world database");
            
        } catch (SQLException e) {
            fail("Database connection should succeed in CI environment: " + e.getMessage());
        }
    }
} 