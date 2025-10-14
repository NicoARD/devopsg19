package com.napier.sem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for basic application functionality
 */
class AppTest {

    private String originalHost;
    private String originalPort;
    private String originalName;
    private String originalUser;
    private String originalPassword;

    @BeforeEach
    void setUp() {
        // Backup original system properties
        originalHost = System.getProperty("database.host");
        originalPort = System.getProperty("database.port");
        originalName = System.getProperty("database.name");
        originalUser = System.getProperty("database.user");
        originalPassword = System.getProperty("database.password");
    }

    @AfterEach
    void tearDown() {
        // Restore original system properties
        setPropertyIfNotNull("database.host", originalHost);
        setPropertyIfNotNull("database.port", originalPort);
        setPropertyIfNotNull("database.name", originalName);
        setPropertyIfNotNull("database.user", originalUser);
        setPropertyIfNotNull("database.password", originalPassword);
    }

    private void setPropertyIfNotNull(String key, String value) {
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.clearProperty(key);
        }
    }

    @Test
    @DisplayName("Should have main method for application entry point")
    void testMainMethodExists() {
        assertDoesNotThrow(() -> {
            App.class.getDeclaredMethod("main", String[].class);
        }, "App class should have a main method");
    }

    @Test
    @DisplayName("Should handle empty command line arguments")
    void testMainWithEmptyArgs() {
        assertDoesNotThrow(() -> {
            App.main(new String[]{});
        }, "App should handle empty command line arguments gracefully");
    }

    @Test
    @DisplayName("Should handle test database argument")
    void testMainWithTestDbArg() {
        // Set up test database properties
        System.setProperty("database.host", "localhost");
        System.setProperty("database.port", "3306");
        System.setProperty("database.name", "world");
        System.setProperty("database.user", "root");
        System.setProperty("database.password", "example");

        assertDoesNotThrow(() -> {
            App.main(new String[]{"--test-db"});
        }, "App should handle --test-db argument");
    }

    @Test
    @DisplayName("Should handle validate argument")
    void testMainWithValidateArg() {
        assertDoesNotThrow(() -> {
            App.main(new String[]{"--validate"});
        }, "App should handle --validate argument");
    }

    @Test
    @DisplayName("Should validate application constants")
    void testApplicationConstants() {
        // Test that the App class is properly defined
        assertNotNull(App.class, "App class should exist");
        assertEquals("com.napier.sem.App", App.class.getName(), "App class should have correct package");
    }

    @Test
    @DisplayName("Should validate package structure")
    void testPackageStructure() {
        // Verify core classes exist in the correct package
        assertDoesNotThrow(() -> {
            Class.forName("com.napier.sem.App");
            Class.forName("com.napier.sem.DatabaseConfig");
            Class.forName("com.napier.sem.CommandRegistry");
            Class.forName("com.napier.sem.ICommand");
        }, "All core classes should be available");
    }

    @Test
    @DisplayName("Should validate command registry functionality")
    void testCommandRegistryExists() {
        assertDoesNotThrow(() -> {
            CommandRegistry registry = new CommandRegistry();
            assertNotNull(registry, "CommandRegistry should be instantiable");
        }, "CommandRegistry should be instantiable without errors");
    }
}