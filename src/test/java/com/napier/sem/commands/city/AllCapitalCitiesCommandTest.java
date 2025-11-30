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
 * Unit tests for AllCapitalCitiesCommand
 * Tests database interactions, error handling, and output formatting
 */
class AllCapitalCitiesCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCapitalCitiesCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCapitalCitiesCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should have correct execution command")
    void testGetExcecutionCommand() {
        assertEquals("all-capitals", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have meaningful description")
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("capital cities"));
        assertTrue(command.getDescription().contains("world"));
    }

    // ---- Successful Execution Tests ----

    @Test
    @DisplayName("Should execute successfully with valid data")
    void testExecuteWithValidData() throws SQLException {
        String[] args = {"all-capitals"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Seoul", "Mumbai", "Mexico City");
        when(mockResultSet.getString("Country")).thenReturn("South Korea", "India", "Mexico");
        when(mockResultSet.getLong("Population")).thenReturn(9_981_619L, 10_500_000L, 8_591_309L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("All Capital Cities in the World"));
        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("Mexico City"));
    }

    @Test
    @DisplayName("Should display capitals in descending order by population")
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Tokyo", "London", "Berlin");
        when(mockResultSet.getString("Country")).thenReturn("Japan", "United Kingdom", "Germany");
        when(mockResultSet.getLong("Population")).thenReturn(7_980_230L, 7_285_000L, 3_386_667L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("Tokyo"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Berlin"));
        // Verify populations are formatted correctly with commas
        assertTrue(output.contains("7,980,230"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("3,386,667"));
    }

    @Test
    @DisplayName("Should execute without errors when no arguments provided")
    void testExecuteWithNoAdditionalArguments() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @DisplayName("Should handle single capital city result")
    void testSingleResult() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("TestCapital");
        when(mockResultSet.getString("Country")).thenReturn("TestCountry");
        when(mockResultSet.getLong("Population")).thenReturn(1_000_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("TestCapital"));
        assertTrue(output.contains("TestCountry"));
        assertTrue(output.contains("1,000,000"));
    }

    // ---- Error Handling Tests ----

    @Test
    @DisplayName("Should handle empty result set gracefully")
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No capital cities found"));
    }

    @Test
    @DisplayName("Should throw SQLException when query execution fails")
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    @DisplayName("Should throw SQLException when prepared statement creation fails")
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    @DisplayName("Should handle database connection error")
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle query timeout error")
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-capitals"};
        
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
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Cairo");
        when(mockResultSet.getString("Country")).thenReturn("Egypt");
        when(mockResultSet.getLong("Population")).thenReturn(15_000_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("15,000,000"));
    }

    @Test
    @DisplayName("Should display multiple capitals correctly")
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Beijing", "Moscow", "Paris", "Rome");
        when(mockResultSet.getString("Country")).thenReturn("China", "Russia", "France", "Italy");
        when(mockResultSet.getLong("Population")).thenReturn(11_716_000L, 10_381_222L, 2_125_246L, 2_643_581L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Beijing"));
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("Paris"));
        assertTrue(output.contains("Rome"));
        assertTrue(output.contains("11,716,000"));
        assertTrue(output.contains("10,381,222"));
        assertTrue(output.contains("2,125,246"));
        assertTrue(output.contains("2,643,581"));
    }

    @Test
    @DisplayName("Should handle capitals with special characters")
    void testCapitalsWithSpecialCharacters() throws SQLException {
        String[] args = {"all-capitals"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("São Paulo");
        when(mockResultSet.getString("Country")).thenReturn("Brazil");
        when(mockResultSet.getLong("Population")).thenReturn(9_968_485L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("São Paulo"));
        assertTrue(output.contains("Brazil"));
    }

    @Test
    @DisplayName("Should execute with extra arguments without errors")
    void testExecuteWithExtraArguments() throws SQLException {
        String[] args = {"all-capitals", "extra", "arguments"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
