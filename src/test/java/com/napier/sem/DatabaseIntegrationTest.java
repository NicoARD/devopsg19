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

    @Test
    @DisplayName("Should validate database metadata in CI")
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void testDatabaseMetadataInCI() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            var metaData = connection.getMetaData();
            
            assertNotNull(metaData.getDatabaseProductName(), "Database product name should not be null");
            assertNotNull(metaData.getDatabaseProductVersion(), "Database version should not be null"); 
            assertNotNull(metaData.getDriverName(), "Driver name should not be null");
            assertNotNull(metaData.getURL(), "Connection URL should not be null");
            assertNotNull(metaData.getUserName(), "Username should not be null");
            
            assertTrue(metaData.getDatabaseProductName().toLowerCase().contains("mysql"), 
                      "Should be connected to MySQL database");
            assertEquals("root", metaData.getUserName(), "Should be connected as root user");
            
        } catch (SQLException e) {
            fail("Database metadata access should succeed in CI: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should validate world database structure in CI")
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void testWorldDatabaseStructure() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            // Check if country table exists
            var metaData = connection.getMetaData();
            var tables = metaData.getTables("world", null, "country", null);
            
            assertTrue(tables.next(), "Country table should exist in world database");
            
        } catch (SQLException e) {
            fail("World database structure validation should succeed: " + e.getMessage());
        }
    }
} 