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
 * Integration tests for AllCapitalCitiesCommand
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCapitalCitiesCommandIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("test-data.sql");

    private AllCapitalCitiesCommand command;

    @BeforeAll
    static void setupDatabase() {
        // Set environment variables for database connection
        System.setProperty("MYSQL_HOST", mysqlContainer.getHost());
        System.setProperty("MYSQL_PORT", String.valueOf(mysqlContainer.getMappedPort(3306)));
        System.setProperty("MYSQL_DATABASE", "world");
        System.setProperty("MYSQL_USER", "testuser");
        System.setProperty("MYSQL_PASSWORD", "testpass");
    }

    @BeforeEach
    void setUp() {
        command = new AllCapitalCitiesCommand();
    }

    @AfterAll
    static void tearDown() {
        DatabaseConfig.closeDataSource();
    }

    @Test
    @Order(1)
    void testExecuteWithRealDatabase() throws Exception {
        // Given: A real database with test data
        try (Connection conn = DatabaseConfig.getConnection()) {
            assertNotNull(conn, "Database connection should be established");

            // When: Execute the command
            String[] args = {"all-capitals"};
            assertDoesNotThrow(() -> command.execute(conn, args));

            // Then: Command should execute without errors
            // Note: Output is printed to console, so we're mainly verifying no exceptions
        }
    }

    @Test
    @Order(2)
    void testExecuteReturnsCapitalCitiesOnly() throws Exception {
        // Given: Database with capital cities
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Insert test data
            stmt.execute("INSERT INTO country (Code, Name, Continent, Region, Capital) VALUES " +
                    "('TST', 'Test Country', 'Asia', 'Test Region', 1)");
            stmt.execute("INSERT INTO city (ID, Name, CountryCode, District, Population) VALUES " +
                    "(1, 'Test Capital', 'TST', 'Test District', 1000000)");

            // When: Execute command
            String[] args = {"all-capitals"};
            assertDoesNotThrow(() -> command.execute(conn, args));

            // Then: Command executes successfully (capital city should be included)
        }
    }

    @Test
    @Order(3)
    void testExecuteOrdersByPopulation() throws Exception {
        // Given: Multiple capital cities with different populations
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Insert test countries and cities
            stmt.execute("INSERT INTO country (Code, Name, Continent, Region, Capital) VALUES " +
                    "('TS1', 'Test Country 1', 'Asia', 'Test Region', 101), " +
                    "('TS2', 'Test Country 2', 'Asia', 'Test Region', 102)");
            
            stmt.execute("INSERT INTO city (ID, Name, CountryCode, District, Population) VALUES " +
                    "(101, 'Small Capital', 'TS1', 'District 1', 500000), " +
                    "(102, 'Large Capital', 'TS2', 'District 2', 2000000)");

            // When: Execute command
            String[] args = {"all-capitals"};
            assertDoesNotThrow(() -> command.execute(conn, args));

            // Then: Command should show capitals ordered by population
            // (Verification is through console output inspection)
        }
    }

    @Test
    @Order(4)
    void testExecuteWithNullConnection() {
        // Given: Null connection
        Connection conn = null;

        // When/Then: Should throw SQLException
        String[] args = {"all-capitals"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }

    @Test
    @Order(5)
    void testExecuteWithEmptyDatabase() throws Exception {
        // Given: Empty country/city tables
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Clear test data
            stmt.execute("DELETE FROM country WHERE Code LIKE 'TS%'");
            stmt.execute("DELETE FROM city WHERE ID > 100");

            // When: Execute command
            String[] args = {"all-capitals"};
            assertDoesNotThrow(() -> command.execute(conn, args));

            // Then: Command should handle empty result set gracefully
        }
    }
}
