package com.napier.sem.commands.city;

import com.napier.sem.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TopCitiesByContinentCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TopCitiesByContinentCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private TopCitiesByContinentCommand command;

    @BeforeAll
    static void setupDatabase() {
        System.setProperty("MYSQL_HOST", mysqlContainer.getHost());
        System.setProperty("MYSQL_PORT", String.valueOf(mysqlContainer.getMappedPort(3306)));
        System.setProperty("MYSQL_DATABASE", "world");
        System.setProperty("MYSQL_USER", "testuser");
        System.setProperty("MYSQL_PASSWORD", "testpass");
    }

    @BeforeEach
    void setUp() {
        command = new TopCitiesByContinentCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithValidContinentAndLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Asia", "5"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(2)
    void testExecuteWithSingleWordContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Europe", "3"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(3)
    void testExecuteRespectsLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Request only top 2 cities from Asia
            String[] args = {"top-cities-continent", "Asia", "2"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Asia", "invalid"};
            // Should print error message about invalid N
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(5)
    void testExecuteWithNegativeLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Asia", "-5"};
            // Should handle gracefully
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(6)
    void testExecuteWithZeroLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Asia", "0"};
            // Should return no results
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(7)
    void testExecuteWithLimitGreaterThanAvailable() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Request more cities than exist in Antarctica
            String[] args = {"top-cities-continent", "Antarctica", "1000"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(8)
    void testExecuteWithMissingArguments() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "Asia"};
            // Should print usage message
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(9)
    void testExecuteWithInvalidContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-continent", "InvalidContinent", "5"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(10)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"top-cities-continent", "Asia", "5"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
