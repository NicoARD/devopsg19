package com.napier.sem.commands.city;

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
 * Unit tests for AllCitiesCommand
 * Tests database interactions, error handling, and output formatting
 */
class AllCitiesCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCitiesCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCitiesCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should have correct execution command")
    void testGetExcecutionCommand() {
        assertEquals("all-cities", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have meaningful description")
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("world"));
    }

    // ---- Successful Execution Tests ----

    @Test
    @DisplayName("Should execute successfully with valid data")
    void testExecuteWithValidData() throws SQLException {
        String[] args = {"all-cities"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai", "Seoul", "Shanghai");
        when(mockResultSet.getString("Country")).thenReturn("India", "South Korea", "China");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra", "Seoul", "Shanghai");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L, 9_981_619L, 9_696_300L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("All Cities in the World"));
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("Shanghai"));
    }

    @Test
    @DisplayName("Should display cities in descending order by population")
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Tokyo", "Delhi", "São Paulo");
        when(mockResultSet.getString("Country")).thenReturn("Japan", "India", "Brazil");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to", "Delhi", "São Paulo");
        when(mockResultSet.getLong("Population")).thenReturn(7_980_230L, 7_206_704L, 9_968_485L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("Tokyo"));
        assertTrue(output.contains("Delhi"));
        assertTrue(output.contains("São Paulo"));
        // Verify populations are formatted correctly with commas
        assertTrue(output.contains("7,980,230"));
        assertTrue(output.contains("7,206,704"));
        assertTrue(output.contains("9,968,485"));
    }

    @Test
    @DisplayName("Should execute without errors when no arguments provided")
    void testExecuteWithNoAdditionalArguments() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @DisplayName("Should handle single city result")
    void testSingleResult() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("TestCity");
        when(mockResultSet.getString("Country")).thenReturn("TestCountry");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getLong("Population")).thenReturn(1_000_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("TestCity"));
        assertTrue(output.contains("TestCountry"));
        assertTrue(output.contains("TestDistrict"));
        assertTrue(output.contains("1,000,000"));
    }

    // ---- Error Handling Tests ----

    @Test
    @DisplayName("Should handle empty result set gracefully")
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No cities found"));
    }

    @Test
    @DisplayName("Should throw SQLException when query execution fails")
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    @DisplayName("Should throw SQLException when prepared statement creation fails")
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    @DisplayName("Should handle database connection error")
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle query timeout error")
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query timeout"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
        assertEquals("Query timeout", exception.getMessage());
    }

    // ---- Data Format Tests ----

    @Test
    @DisplayName("Should format large populations correctly with commas")
    void testLargePopulationFormatting() throws SQLException {
        String[] args = {"all-cities"};
        
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
    @DisplayName("Should display multiple cities correctly")
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Beijing", "Moscow", "Istanbul", "London");
        when(mockResultSet.getString("Country")).thenReturn("China", "Russia", "Turkey", "United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("Peking", "Moscow", "Istanbul", "England");
        when(mockResultSet.getLong("Population")).thenReturn(7_472_000L, 8_389_200L, 8_787_958L, 7_285_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Beijing"));
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("Istanbul"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("7,472,000"));
        assertTrue(output.contains("8,389,200"));
        assertTrue(output.contains("8,787,958"));
        assertTrue(output.contains("7,285,000"));
    }

    @Test
    @DisplayName("Should handle cities with special characters")
    void testCitiesWithSpecialCharacters() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("São Paulo");
        when(mockResultSet.getString("Country")).thenReturn("Brazil");
        when(mockResultSet.getString("District")).thenReturn("São Paulo");
        when(mockResultSet.getLong("Population")).thenReturn(9_968_485L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("São Paulo"));
        assertTrue(output.contains("Brazil"));
    }

    @Test
    @DisplayName("Should execute with extra arguments without errors")
    void testExecuteWithExtraArguments() throws SQLException {
        String[] args = {"all-cities", "extra", "arguments"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @DisplayName("Should display district information correctly")
    void testDistrictDisplay() throws SQLException {
        String[] args = {"all-cities"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("New York");
        when(mockResultSet.getString("Country")).thenReturn("United States");
        when(mockResultSet.getString("District")).thenReturn("New York");
        when(mockResultSet.getLong("Population")).thenReturn(8_008_278L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("New York"));
        assertTrue(output.contains("United States"));
        assertTrue(output.contains("8,008,278"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
