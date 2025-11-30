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
 * Unit tests for CapitalCitiesByContinentCommand
 * Tests input validation, error handling, and database interactions
 */
class CapitalCitiesByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private CapitalCitiesByContinentCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new CapitalCitiesByContinentCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("capital-cities-continent", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("capital cities"));
        assertTrue(command.getDescription().contains("continent"));
    }

    // ---- Input Validation Tests ----

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"capital-cities-continent"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a continent name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    void testExecuteWithEmptyContinent() throws SQLException {
        String[] args = {"capital-cities-continent", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    void testExecuteWithValidContinent() throws SQLException {
        String[] args = {"capital-cities-continent", "Europe"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Berlin", "Paris");
        when(mockResultSet.getString("Country")).thenReturn("Germany", "France");
        when(mockResultSet.getLong("Population")).thenReturn(3_500_000L, 2_200_000L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).setString(1, "Europe");
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Europe"));
        assertTrue(output.contains("Berlin"));
        assertTrue(output.contains("Paris"));
    }

    // ---- Error Handling Tests ----

    @Test
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"capital-cities-continent", "InvalidContinent"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No capital cities found"));
        assertTrue(output.contains("InvalidContinent"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"capital-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"capital-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    void testContinentParameterWithSpaces() throws SQLException {
        String[] args = {"capital-cities-continent", "  North America  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the continent name
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testContinentParameterCaseSensitive() throws SQLException {
        String[] args = {"capital-cities-continent", "ASIA"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the continent as provided
        verify(mockStatement).setString(1, "ASIA");
    }

    @Test
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"capital-cities-continent", "Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Tokyo", "Beijing", "Seoul");
        when(mockResultSet.getString("Country")).thenReturn("Japan", "China", "South Korea");
        when(mockResultSet.getLong("Population")).thenReturn(13_960_000L, 11_716_000L, 9_963_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Tokyo"));
        assertTrue(output.contains("Beijing"));
        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("13,960,000"));
        assertTrue(output.contains("11,716,000"));
        assertTrue(output.contains("9,963,000"));
    }

    @Test
    void testMultiWordContinentName() throws SQLException {
        String[] args = {"capital-cities-continent", "North", "America"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should join multi-word continent names
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"capital-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Moscow", "London", "Berlin");
        when(mockResultSet.getString("Country")).thenReturn("Russia", "United Kingdom", "Germany");
        when(mockResultSet.getLong("Population")).thenReturn(10_381_222L, 7_285_000L, 3_386_667L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Berlin"));
        // Verify populations are formatted correctly
        assertTrue(output.contains("10,381,222"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("3,386,667"));
    }

    @Test
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"capital-cities-continent", "Africa"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void testQueryExecutionError() throws SQLException {
        String[] args = {"capital-cities-continent", "Oceania"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query timeout"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
        assertEquals("Query timeout", exception.getMessage());
    }

    @Test
    void testSingleResult() throws SQLException {
        String[] args = {"capital-cities-continent", "Antarctica"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("TestCapital");
        when(mockResultSet.getString("Country")).thenReturn("TestCountry");
        when(mockResultSet.getLong("Population")).thenReturn(100_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("TestCapital"));
        assertTrue(output.contains("TestCountry"));
        assertTrue(output.contains("100,000"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
