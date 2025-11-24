package com.napier.sem.commands.region;

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
 * Unit tests for CapitalCitiesByRegionCommand
 * Tests input validation, error handling, and database interactions
 */
class CapitalCitiesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private CapitalCitiesByRegionCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new CapitalCitiesByRegionCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("capital-cities-region", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("capital cities"));
        assertTrue(command.getDescription().contains("region"));
    }

    // ---- Input Validation Tests ----

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"capital-cities-region"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a region name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    void testExecuteWithEmptyRegion() throws SQLException {
        String[] args = {"capital-cities-region", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    void testExecuteWithValidRegion() throws SQLException {
        String[] args = {"capital-cities-region", "Europe"};
        
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
        String[] args = {"capital-cities-region", "InvalidRegion"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No capital cities found"));
        assertTrue(output.contains("InvalidRegion"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"capital-cities-region", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"capital-cities-region", "Europe"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    void testRegionParameterWithSpaces() throws SQLException {
        String[] args = {"capital-cities-region", "  Middle East  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the region name
        verify(mockStatement).setString(1, "Middle East");
    }

    @Test
    void testRegionParameterCaseSensitive() throws SQLException {
        String[] args = {"capital-cities-region", "EUROPE"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the region as provided
        verify(mockStatement).setString(1, "EUROPE");
    }

    @Test
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"capital-cities-region", "Asia"};
        
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

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
