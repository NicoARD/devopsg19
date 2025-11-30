package com.napier.sem.commands.continent;

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
 * Unit tests for TopCitiesByContinentCommand
 */
class TopCitiesByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private TopCitiesByContinentCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        command = new TopCitiesByContinentCommand();

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
        assertEquals("top-cities-continent", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have meaningful description")
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("continent"));
    }

    @Test
    @DisplayName("Should handle missing arguments")
    void testMissingArguments() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Usage"));
    }

    @Test
    @DisplayName("Should handle invalid number input")
    void testInvalidNumberInput() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent", "Asia", "abc"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be a valid integer"));
    }

    @Test
    @DisplayName("Should handle negative number")
    void testNegativeNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent", "Europe", "-5"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be greater than zero"));
    }

    @Test
    @DisplayName("Should handle zero")
    void testZeroNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent", "Europe", "0"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("must be greater than zero"));
    }

    @Test
    @DisplayName("Should execute successfully with valid input")
    void testValidInput() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai", "Shanghai");
        when(mockResultSet.getString("Country")).thenReturn("India", "China");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra", "Shanghai");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L, 9_696_300L);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent", "Asia", "5"}));

        verify(mockStatement).setString(1, "Asia");
        verify(mockStatement).setInt(2, 5);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("Shanghai"));
        assertTrue(output.contains("10,500,000"));
        assertTrue(output.contains("9,696,300"));
    }

    @Test
    @DisplayName("Should handle no results")
    void testNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-cities-continent", "Antarctica", "10"}));
        
        String output = outputStream.toString();
        assertTrue(output.contains("No results found"));
        assertTrue(output.contains("Antarctica"));
    }

    @Test
    @DisplayName("Should throw SQLException on database error")
    void testDatabaseError() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> command.execute(mockConnection, new String[]{"top-cities-continent", "Europe", "5"}));
        
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

        command.execute(mockConnection, new String[]{"top-cities-continent", "Asia", "1"});
        
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
            () -> command.execute(mockConnection, new String[]{"top-cities-continent", "Africa", "10"}));
    }

    @Test
    @DisplayName("Should format populations with commas")
    void testPopulationFormatting() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Cairo");
        when(mockResultSet.getString("Country")).thenReturn("Egypt");
        when(mockResultSet.getString("District")).thenReturn("Kairo");
        when(mockResultSet.getLong("Population")).thenReturn(6_789_479L);

        command.execute(mockConnection, new String[]{"top-cities-continent", "Africa", "5"});
        
        String output = outputStream.toString();
        assertTrue(output.contains("6,789,479"));
    }

    @Test
    @DisplayName("Should handle multiple cities correctly")
    void testMultipleCities() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Moscow", "London", "Berlin");
        when(mockResultSet.getString("Country")).thenReturn("Russia", "United Kingdom", "Germany");
        when(mockResultSet.getString("District")).thenReturn("Moscow", "England", "Berliini");
        when(mockResultSet.getLong("Population")).thenReturn(8_389_200L, 7_285_000L, 3_386_667L);

        command.execute(mockConnection, new String[]{"top-cities-continent", "Europe", "3"});

        String output = outputStream.toString();
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Berlin"));
        assertTrue(output.contains("8,389,200"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("3,386,667"));
    }

    @Test
    @DisplayName("Should verify correct SQL parameters")
    void testSQLParameters() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-continent", "South America", "15"});

        verify(mockStatement).setString(1, "South America");
        verify(mockStatement).setInt(2, 15);
    }

    @Test
    @DisplayName("Should handle multi-word continent names")
    void testMultiWordContinentName() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-continent", "North", "America", "10"});

        verify(mockStatement).setString(1, "North America");
        verify(mockStatement).setInt(2, 10);
    }

    @Test
    @DisplayName("Should handle single word continent names")
    void testSingleWordContinentName() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        command.execute(mockConnection, new String[]{"top-cities-continent", "Europe", "20"});

        verify(mockStatement).setString(1, "Europe");
        verify(mockStatement).setInt(2, 20);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
