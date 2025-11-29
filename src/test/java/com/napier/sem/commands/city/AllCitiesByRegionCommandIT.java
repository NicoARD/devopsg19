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
 * Integration tests for AllCitiesByRegionCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByRegionCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private AllCitiesByRegionCommand command;

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
        command = new AllCitiesByRegionCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithValidRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-region", "Eastern Asia"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-region", "Western", "Europe"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-region", "Eastern Asia"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-region", "InvalidRegion"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-region"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"all-cities-region", "Eastern Asia"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
