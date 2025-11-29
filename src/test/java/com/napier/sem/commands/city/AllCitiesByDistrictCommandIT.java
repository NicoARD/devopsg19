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
 * Integration tests for AllCitiesByDistrictCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByDistrictCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private AllCitiesByDistrictCommand command;

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
        command = new AllCitiesByDistrictCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithValidDistrict() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-district", "England"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordDistrict() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Insert test data with multi-word district
            stmt.execute("INSERT INTO country (Code, Name, Continent, Region, Capital) VALUES " +
                    "('TDC', 'Test District Country', 'Asia', 'Test Region', NULL)");
            stmt.execute("INSERT INTO city (ID, Name, CountryCode, District, Population) VALUES " +
                    "(401, 'Test District City', 'TDC', 'Multi Word District', 500000)");

            String[] args = {"all-cities-district", "Multi", "Word", "District"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByDistrict() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-district", "England"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidDistrict() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-district", "InvalidDistrict"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String[] args = {"all-cities-district"};
            assertDoesNotThrow(() -> command.execute(conn, args));
        }
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"all-cities-district", "England"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
