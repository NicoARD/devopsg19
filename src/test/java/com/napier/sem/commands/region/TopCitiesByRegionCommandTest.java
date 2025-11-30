package com.napier.sem.commands.region;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TopCitiesByRegionCommand
 */
class TopCitiesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private TopCitiesByRegionCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        command = new TopCitiesByRegionCommand();

        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    @DisplayName("Should have correct execution command")
    void testGetExcecutionCommand() {
        assertEquals("top-cities-region", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have meaningful description")
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("region"));
    }

    @Test
    @DisplayName("Should handle missing arguments")
    void testInvalidArgs() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Usage"));
    }

    @Test
    @DisplayName("Should handle invalid number format")
    void testInvalidNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region", "Europe", "XYZ"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be a valid integer"));
    }

    @Test
    @DisplayName("Should handle negative number")
    void testNegativeNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region", "Asia", "-5"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be greater than zero"));
    }

    @Test
    @DisplayName("Should handle zero")
    void testZeroNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region", "Asia", "0"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be greater than zero"));
    }

    @Test
    @DisplayName("Should execute successfully with valid input")
    void testValidInputDoesNotThrow() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai", "Delhi");
        when(mockResultSet.getString("Country")).thenReturn("India", "India");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra", "Delhi");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L, 7_206_704L);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region", "Southern and Central Asia", "5"}));

        verify(mockStatement).setString(1, "Southern and Central Asia");
        verify(mockStatement).setInt(2, 5);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("Delhi"));
        assertTrue(output.contains("10,500,000"));
        assertTrue(output.contains("7,206,704"));
    }

    @Test
    @DisplayName("Should handle no results")
    void testNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-region", "UnknownRegion", "10"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("No results found"));
        assertTrue(output.contains("UnknownRegion"));
    }

    @Test
    @DisplayName("Should throw SQLException on database error")
    void testDatabaseError() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> command.execute(mockConnection, new String[]{"top-cities-region", "Europe", "5"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    @DisplayName("Should display all columns correctly")
    void testAllColumnsDisplayed() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("Japan");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to");
        when(mockResultSet.getLong("Population")).thenReturn(7_980_230L);

        command.execute(mockConnection, new String[]{"top-cities-region", "Eastern Asia", "1"});
        
        String output = outputStream.toString();
        assertTrue(output.contains("Tokyo"));
        assertTrue(output.contains("Japan"));
        assertTrue(output.contains("Tokyo-to"));
        assertTrue(output.contains("7,980,230"));
    }

    @Test
    @DisplayName("Should handle prepared statement creation failure")
    void testPreparedStatementFailure() throws SQLException {
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection failed"));

        assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, new String[]{"top-cities-region", "Caribbean", "10"}));
    }

    @Test
    @DisplayName("Should format populations with commas")
    void testPopulationFormatting() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Istanbul");
        when(mockResultSet.getString("Country")).thenReturn("Turkey");
        when(mockResultSet.getString("District")).thenReturn("Istanbul");
        when(mockResultSet.getLong("Population")).thenReturn(8_787_958L);

        command.execute(mockConnection, new String[]{"top-cities-region", "Middle East", "5"});
        
        String output = outputStream.toString();
        assertTrue(output.contains("8,787,958"));
    }

    @Test
    @DisplayName("Should handle multiple cities correctly")
    void testMultipleCities() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("London", "Birmingham", "Leeds");
        when(mockResultSet.getString("Country")).thenReturn("United Kingdom", "United Kingdom", "United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("England", "England", "England");
        when(mockResultSet.getLong("Population")).thenReturn(7_285_000L, 1_013_000L, 424_194L);

        command.execute(mockConnection, new String[]{"top-cities-region", "British Islands", "3"});

        String output = outputStream.toString();
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Birmingham"));
        assertTrue(output.contains("Leeds"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("1,013,000"));
        assertTrue(output.contains("424,194"));
    }

    @Test
    @DisplayName("Should verify correct SQL parameters")
    void testSQLParameters() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-region", "Western Europe", "20"});

        verify(mockStatement).setString(1, "Western Europe");
        verify(mockStatement).setInt(2, 20);
    }

    @Test
    @DisplayName("Should handle multi-word region names")
    void testMultiWordRegionName() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-region", "Eastern", "Asia", "10"});

        verify(mockStatement).setString(1, "Eastern Asia");
        verify(mockStatement).setInt(2, 10);
    }

    @Test
    @DisplayName("Should handle three-word region names")
    void testThreeWordRegionName() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-region", "Southern", "and", "Central", "Asia", "15"});

        verify(mockStatement).setString(1, "Southern and Central Asia");
        verify(mockStatement).setInt(2, 15);
    }

    @Test
    @DisplayName("Should handle special characters in region names")
    void testSpecialCharactersInRegion() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("São Paulo");
        when(mockResultSet.getString("Country")).thenReturn("Brazil");
        when(mockResultSet.getString("District")).thenReturn("São Paulo");
        when(mockResultSet.getLong("Population")).thenReturn(9_968_485L);

        command.execute(mockConnection, new String[]{"top-cities-region", "South America", "5"});
        
        String output = outputStream.toString();
        assertTrue(output.contains("São Paulo"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
