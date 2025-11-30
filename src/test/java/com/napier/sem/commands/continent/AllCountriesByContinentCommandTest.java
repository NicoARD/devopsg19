package com.napier.sem.commands.continent;

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
 * Unit tests for AllCountriesByContinentCommand
 * Tests input validation, error handling, and database interactions
 */
class AllCountriesByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCountriesByContinentCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCountriesByContinentCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("all-countries-continent", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("countries"));
        assertTrue(command.getDescription().contains("continent"));
    }

    // ---- Input Validation Tests ----

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"all-countries-continent"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a continent name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    void testExecuteWithEmptyContinent() throws SQLException {
        String[] args = {"all-countries-continent", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    void testExecuteWithValidContinent() throws SQLException {
        String[] args = {"all-countries-continent", "Europe"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU", "FRA");
        when(mockResultSet.getString("Name")).thenReturn("Germany", "France");
        when(mockResultSet.getString("Continent")).thenReturn("Europe", "Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe", "Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(82_164_700L, 59_225_700L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).setString(1, "Europe");
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Europe"));
        assertTrue(output.contains("Germany"));
        assertTrue(output.contains("France"));
    }

    // ---- Error Handling Tests ----

    @Test
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-countries-continent", "InvalidContinent"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No countries found"));
        assertTrue(output.contains("InvalidContinent"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-countries-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-countries-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    void testContinentParameterWithSpaces() throws SQLException {
        String[] args = {"all-countries-continent", "  North America  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the continent name
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testContinentParameterCaseSensitive() throws SQLException {
        String[] args = {"all-countries-continent", "ASIA"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the continent as provided
        verify(mockStatement).setString(1, "ASIA");
    }

    @Test
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-countries-continent", "Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("CHN", "IND", "IDN");
        when(mockResultSet.getString("Name")).thenReturn("China", "India", "Indonesia");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Asia", "Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia", "Southern and Central Asia", "Southeast Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1_277_558_000L, 1_013_662_000L, 212_107_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("China"));
        assertTrue(output.contains("India"));
        assertTrue(output.contains("Indonesia"));
        assertTrue(output.contains("1,277,558,000"));
        assertTrue(output.contains("1,013,662,000"));
        assertTrue(output.contains("212,107,000"));
    }

    @Test
    void testMultiWordContinentName() throws SQLException {
        String[] args = {"all-countries-continent", "North", "America"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should join multi-word continent names
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-countries-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("RUS", "DEU", "GBR");
        when(mockResultSet.getString("Name")).thenReturn("Russia", "Germany", "United Kingdom");
        when(mockResultSet.getString("Continent")).thenReturn("Europe", "Europe", "Europe");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Europe", "Western Europe", "British Islands");
        when(mockResultSet.getLong("Population")).thenReturn(146_934_000L, 82_164_700L, 59_623_400L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all countries are displayed
        assertTrue(output.contains("Russia"));
        assertTrue(output.contains("Germany"));
        assertTrue(output.contains("United Kingdom"));
        // Verify populations are formatted correctly
        assertTrue(output.contains("146,934,000"));
        assertTrue(output.contains("82,164,700"));
        assertTrue(output.contains("59,623,400"));
    }

    @Test
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-countries-continent", "Africa"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-countries-continent", "Oceania"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query timeout"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
        assertEquals("Query timeout", exception.getMessage());
    }

    @Test
    void testSingleCountryResult() throws SQLException {
        String[] args = {"all-countries-continent", "Antarctica"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("ATA");
        when(mockResultSet.getString("Name")).thenReturn("Antarctica");
        when(mockResultSet.getString("Continent")).thenReturn("Antarctica");
        when(mockResultSet.getString("Region")).thenReturn("Antarctica");
        when(mockResultSet.getLong("Population")).thenReturn(0L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Antarctica"));
    }

    @Test
    void testAllColumnsDisplayed() throws SQLException {
        String[] args = {"all-countries-continent", "Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("JPN");
        when(mockResultSet.getString("Name")).thenReturn("Japan");
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia");
        when(mockResultSet.getLong("Population")).thenReturn(126_714_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("JPN"));
        assertTrue(output.contains("Japan"));
        assertTrue(output.contains("Asia"));
        assertTrue(output.contains("Eastern Asia"));
        assertTrue(output.contains("126,714,000"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
