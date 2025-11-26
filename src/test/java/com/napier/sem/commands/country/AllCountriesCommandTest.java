package com.napier.sem.commands.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * Unit tests for AllCountriesCommand
 * Tests error handling and database interactions
 */
class AllCountriesCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCountriesCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCountriesCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("all-countries", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("countries"));
        assertTrue(command.getDescription().contains("world"));
    }

    // ---- Success Tests ----

    @Test
    void testExecuteWithValidData() throws SQLException {
        String[] args = {"all-countries"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("CHN", "IND");
        when(mockResultSet.getString("Name")).thenReturn("China", "India");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia", "Southern and Central Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1_277_558_000L, 1_013_662_000L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("All Countries in the World"));
        assertTrue(output.contains("China"));
        assertTrue(output.contains("India"));
        assertTrue(output.contains("1,277,558,000"));
        assertTrue(output.contains("1,013,662,000"));
    }

    @Test
    void testExecuteWithMultipleCountries() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("CHN", "IND", "USA", "IDN");
        when(mockResultSet.getString("Name")).thenReturn("China", "India", "United States", "Indonesia");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Asia", "North America", "Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia", "Southern and Central Asia", "North America", "Southeast Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1_277_558_000L, 1_013_662_000L, 278_357_000L, 212_107_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("China"));
        assertTrue(output.contains("India"));
        assertTrue(output.contains("United States"));
        assertTrue(output.contains("Indonesia"));
    }

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    // ---- Error Handling Tests ----

    @Test
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No countries found"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query timeout"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
        assertEquals("Query timeout", exception.getMessage());
    }

    // ---- Data Display Tests ----

    @Test
    void testPopulationFormattingWithCommas() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("CHN");
        when(mockResultSet.getString("Name")).thenReturn("China");
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1_277_558_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("1,277,558,000"));
    }

    @Test
    void testAllColumnsDisplayed() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("USA");
        when(mockResultSet.getString("Name")).thenReturn("United States");
        when(mockResultSet.getString("Continent")).thenReturn("North America");
        when(mockResultSet.getString("Region")).thenReturn("North America");
        when(mockResultSet.getLong("Population")).thenReturn(278_357_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("USA"));
        assertTrue(output.contains("United States"));
        assertTrue(output.contains("North America"));
        assertTrue(output.contains("278,357,000"));
    }

    @Test
    void testSingleCountryResult() throws SQLException {
        String[] args = {"all-countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("MCO");
        when(mockResultSet.getString("Name")).thenReturn("Monaco");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(34_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Monaco"));
        assertTrue(output.contains("34,000"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
