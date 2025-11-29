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
 * Integration tests for TopCitiesByRegionCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TopCitiesByRegionCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private TopCitiesByRegionCommand command;

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
        command = new TopCitiesByRegionCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithValidRegionAndLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "5"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(2)
    void testExecuteWithSingleWordRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Insert test region with single word
            stmt.execute("INSERT INTO country (Code, Name, Continent, Region, Capital) VALUES " +
                    "('TRG', 'Test Region Country', 'Asia', 'TestRegion', NULL)");
            stmt.execute("INSERT INTO city (ID, Name, CountryCode, District, Population) VALUES " +
                    "(501, 'Test Region City', 'TRG', 'Test District', 1000000)");

            String[] args = {"top-cities-region", "TestRegion", "3"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(3)
    void testExecuteRespectsLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "2"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "invalid"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(5)
    void testExecuteWithNegativeLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "-5"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(6)
    void testExecuteWithZeroLimit() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "0"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(7)
    void testExecuteWithLimitGreaterThanAvailable() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia", "1000"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(8)
    void testExecuteWithMissingArguments() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "Eastern Asia"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(9)
    void testExecuteWithInvalidRegion() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"top-cities-region", "InvalidRegion", "5"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(10)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"top-cities-region", "Eastern Asia", "5"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
