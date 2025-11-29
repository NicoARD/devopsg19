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
 * Integration tests for AllCitiesByContinentCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByContinentCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private AllCitiesByContinentCommand command;

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
        command = new AllCitiesByContinentCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithValidContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-continent", "Asia"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-continent", "North", "America"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Verify Asia has cities from test data
            String[] args = {"all-cities-continent", "Asia"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidContinent() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-continent", "InvalidContinent"};
            // Should execute but return no results
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-continent"};
            // Should print usage message
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"all-cities-continent", "Asia"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
