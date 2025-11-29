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
 * Unit tests for AllCitiesByRegionCommand
 * Tests input validation, error handling, and database interactions
 */
class AllCitiesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByRegionCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCitiesByRegionCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("all-cities-region", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("region"));
    }

    // ---- Input Validation Tests ----

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"all-cities-region"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a region name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    void testExecuteWithEmptyRegion() throws SQLException {
        String[] args = {"all-cities-region", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    void testExecuteWithValidRegion() throws SQLException {
        String[] args = {"all-cities-region", "Western Europe"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Berlin", "Paris");
        when(mockResultSet.getString("Country")).thenReturn("Germany", "France");
        when(mockResultSet.getString("District")).thenReturn("Berliini", "Île-de-France");
        when(mockResultSet.getLong("Population")).thenReturn(3_386_667L, 2_125_246L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).setString(1, "Western Europe");
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Western Europe"));
        assertTrue(output.contains("Berlin"));
        assertTrue(output.contains("Paris"));
    }

    // ---- Error Handling Tests ----

    @Test
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-cities-region", "InvalidRegion"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No cities found"));
        assertTrue(output.contains("InvalidRegion"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-cities-region", "Eastern Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-cities-region", "Middle East"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    void testRegionParameterWithSpaces() throws SQLException {
        String[] args = {"all-cities-region", "  Middle East  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the region name
        verify(mockStatement).setString(1, "Middle East");
    }

    @Test
    void testRegionParameterCaseSensitive() throws SQLException {
        String[] args = {"all-cities-region", "EASTERN ASIA"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the region as provided
        verify(mockStatement).setString(1, "EASTERN ASIA");
    }

    @Test
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-cities-region", "Eastern Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Shanghai", "Beijing", "Seoul");
        when(mockResultSet.getString("Country")).thenReturn("China", "China", "South Korea");
        when(mockResultSet.getString("District")).thenReturn("Shanghai", "Peking", "Seoul");
        when(mockResultSet.getLong("Population")).thenReturn(9_696_300L, 7_472_000L, 9_981_619L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Shanghai"));
        assertTrue(output.contains("Beijing"));
        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("9,696,300"));
        assertTrue(output.contains("7,472,000"));
        assertTrue(output.contains("9,981,619"));
    }

    @Test
    void testMultiWordRegionName() throws SQLException {
        String[] args = {"all-cities-region", "Middle", "East"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should join multi-word region names
        verify(mockStatement).setString(1, "Middle East");
    }

    @Test
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-cities-region", "Southern Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Istanbul", "Madrid", "Rome");
        when(mockResultSet.getString("Country")).thenReturn("Turkey", "Spain", "Italy");
        when(mockResultSet.getString("District")).thenReturn("Istanbul", "Madrid", "Lazio");
        when(mockResultSet.getLong("Population")).thenReturn(8_787_958L, 2_879_052L, 2_643_581L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("Istanbul"));
        assertTrue(output.contains("Madrid"));
        assertTrue(output.contains("Rome"));
        // Verify populations are formatted correctly
        assertTrue(output.contains("8,787,958"));
        assertTrue(output.contains("2,879,052"));
        assertTrue(output.contains("2,643,581"));
    }

    @Test
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-cities-region", "Caribbean"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-cities-region", "Southeast Asia"};
        
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
        String[] args = {"all-cities-region", "Baltic Countries"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Riga");
        when(mockResultSet.getString("Country")).thenReturn("Latvia");
        when(mockResultSet.getString("District")).thenReturn("Riika");
        when(mockResultSet.getLong("Population")).thenReturn(764_328L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Riga"));
        assertTrue(output.contains("Latvia"));
        assertTrue(output.contains("Riika"));
        assertTrue(output.contains("764,328"));
    }

    @Test
    void testAllColumnsDisplayed() throws SQLException {
        String[] args = {"all-cities-region", "Eastern Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("Japan");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to");
        when(mockResultSet.getLong("Population")).thenReturn(7_980_230L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Tokyo"));
        assertTrue(output.contains("Japan"));
        assertTrue(output.contains("Tokyo-to"));
        assertTrue(output.contains("7,980,230"));
    }

    @Test
    void testLargePopulationFormatting() throws SQLException {
        String[] args = {"all-cities-region", "Southern and Central Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai");
        when(mockResultSet.getString("Country")).thenReturn("India");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("10,500,000"));
    }

    @Test
    void testCitiesWithSpecialCharacters() throws SQLException {
        String[] args = {"all-cities-region", "Western Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Zürich");
        when(mockResultSet.getString("Country")).thenReturn("Switzerland");
        when(mockResultSet.getString("District")).thenReturn("Zürich");
        when(mockResultSet.getLong("Population")).thenReturn(336_800L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Zürich"));
        assertTrue(output.contains("Switzerland"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
