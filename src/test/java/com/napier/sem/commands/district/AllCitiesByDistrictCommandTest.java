package com.napier.sem.commands.district;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AllCitiesByDistrictCommand
 * Tests input validation, error handling, and database interactions
 */
class AllCitiesByDistrictCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByDistrictCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCitiesByDistrictCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should have correct execution command")
    void testGetExcecutionCommand() {
        assertEquals("all-cities-district", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have meaningful description")
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("district"));
    }

    // ---- Input Validation Tests ----

    @Test
    @DisplayName("Should handle missing district argument")
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"all-cities-district"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a district name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    @DisplayName("Should handle empty district name")
    void testExecuteWithEmptyDistrict() throws SQLException {
        String[] args = {"all-cities-district", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    @DisplayName("Should execute successfully with valid district")
    void testExecuteWithValidDistrict() throws SQLException {
        String[] args = {"all-cities-district", "California"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Los Angeles", "San Diego");
        when(mockResultSet.getString("Country")).thenReturn("United States", "United States");
        when(mockResultSet.getString("District")).thenReturn("California", "California");
        when(mockResultSet.getLong("Population")).thenReturn(3_694_820L, 1_223_400L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).setString(1, "California");
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("California"));
        assertTrue(output.contains("Los Angeles"));
        assertTrue(output.contains("San Diego"));
    }

    // ---- Error Handling Tests ----

    @Test
    @DisplayName("Should handle empty result set gracefully")
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-cities-district", "InvalidDistrict"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No cities found"));
        assertTrue(output.contains("InvalidDistrict"));
    }

    @Test
    @DisplayName("Should throw SQLException when query execution fails")
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-cities-district", "Texas"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    @DisplayName("Should throw SQLException when prepared statement creation fails")
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-cities-district", "Ontario"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    @DisplayName("Should handle district parameter with spaces")
    void testDistrictParameterWithSpaces() throws SQLException {
        String[] args = {"all-cities-district", "  New York  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the district name
        verify(mockStatement).setString(1, "New York");
    }

    @Test
    @DisplayName("Should handle district parameter case sensitivity")
    void testDistrictParameterCaseSensitive() throws SQLException {
        String[] args = {"all-cities-district", "CALIFORNIA"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the district as provided
        verify(mockStatement).setString(1, "CALIFORNIA");
    }

    @Test
    @DisplayName("Should display multiple cities correctly")
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-cities-district", "England"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("London", "Birmingham", "Leeds");
        when(mockResultSet.getString("Country")).thenReturn("United Kingdom", "United Kingdom", "United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("England", "England", "England");
        when(mockResultSet.getLong("Population")).thenReturn(7_285_000L, 1_013_000L, 424_194L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Birmingham"));
        assertTrue(output.contains("Leeds"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("1,013,000"));
        assertTrue(output.contains("424,194"));
    }

    @Test
    @DisplayName("Should handle multi-word district names")
    void testMultiWordDistrictName() throws SQLException {
        String[] args = {"all-cities-district", "New", "York"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should join multi-word district names
        verify(mockStatement).setString(1, "New York");
    }

    @Test
    @DisplayName("Should display cities in descending order by population")
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-cities-district", "São Paulo"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("São Paulo", "Guarulhos", "Osasco");
        when(mockResultSet.getString("Country")).thenReturn("Brazil", "Brazil", "Brazil");
        when(mockResultSet.getString("District")).thenReturn("São Paulo", "São Paulo", "São Paulo");
        when(mockResultSet.getLong("Population")).thenReturn(9_968_485L, 1_095_874L, 659_604L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("São Paulo"));
        assertTrue(output.contains("Guarulhos"));
        assertTrue(output.contains("Osasco"));
        // Verify populations are formatted correctly
        assertTrue(output.contains("9,968,485"));
        assertTrue(output.contains("1,095,874"));
        assertTrue(output.contains("659,604"));
    }

    @Test
    @DisplayName("Should handle database connection error")
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-cities-district", "Florida"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle query timeout error")
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-cities-district", "Tokyo-to"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query timeout"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
        assertEquals("Query timeout", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle single city result")
    void testSingleResult() throws SQLException {
        String[] args = {"all-cities-district", "Distrito Capital"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Bogotá");
        when(mockResultSet.getString("Country")).thenReturn("Colombia");
        when(mockResultSet.getString("District")).thenReturn("Distrito Capital");
        when(mockResultSet.getLong("Population")).thenReturn(6_260_862L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Bogotá"));
        assertTrue(output.contains("Colombia"));
        assertTrue(output.contains("Distrito Capital"));
        assertTrue(output.contains("6,260,862"));
    }

    @Test
    @DisplayName("Should display all columns correctly")
    void testAllColumnsDisplayed() throws SQLException {
        String[] args = {"all-cities-district", "Maharashtra"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai");
        when(mockResultSet.getString("Country")).thenReturn("India");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("India"));
        assertTrue(output.contains("Maharashtra"));
        assertTrue(output.contains("10,500,000"));
    }

    @Test
    @DisplayName("Should format large populations with commas")
    void testLargePopulationFormatting() throws SQLException {
        String[] args = {"all-cities-district", "Kairo"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Cairo");
        when(mockResultSet.getString("Country")).thenReturn("Egypt");
        when(mockResultSet.getString("District")).thenReturn("Kairo");
        when(mockResultSet.getLong("Population")).thenReturn(6_789_479L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("6,789,479"));
    }

    @Test
    @DisplayName("Should handle cities with special characters")
    void testCitiesWithSpecialCharacters() throws SQLException {
        String[] args = {"all-cities-district", "Île-de-France"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Paris");
        when(mockResultSet.getString("Country")).thenReturn("France");
        when(mockResultSet.getString("District")).thenReturn("Île-de-France");
        when(mockResultSet.getLong("Population")).thenReturn(2_125_246L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Paris"));
        assertTrue(output.contains("France"));
        assertTrue(output.contains("Île-de-France"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
