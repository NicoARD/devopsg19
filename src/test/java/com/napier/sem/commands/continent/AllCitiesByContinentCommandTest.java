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
 * Unit tests for AllCitiesByContinentCommand
 * Tests input validation, error handling, and database interactions
 */
class AllCitiesByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByContinentCommand command;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new AllCitiesByContinentCommand();
        
        // Capture console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testGetExcecutionCommand() {
        assertEquals("all-cities-continent", command.getExcecutionCommand());
    }

    @Test
    void testGetDescription() {
        assertNotNull(command.getDescription());
        assertTrue(command.getDescription().contains("cities"));
        assertTrue(command.getDescription().contains("continent"));
    }

    // ---- Input Validation Tests ----

    @Test
    void testExecuteWithNoArguments() throws SQLException {
        String[] args = {"all-cities-continent"};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Please provide a continent name"));
        assertTrue(output.contains("Usage"));
    }

    @Test
    void testExecuteWithEmptyContinent() throws SQLException {
        String[] args = {"all-cities-continent", "   "};
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("cannot be empty"));
    }

    @Test
    void testExecuteWithValidContinent() throws SQLException {
        String[] args = {"all-cities-continent", "Europe"};
        
        // Mock the database interaction
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Moscow", "London");
        when(mockResultSet.getString("Country")).thenReturn("Russia", "United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("Moscow", "England");
        when(mockResultSet.getLong("Population")).thenReturn(8_389_200L, 7_285_000L);
        
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        
        verify(mockStatement).setString(1, "Europe");
        verify(mockStatement).executeQuery();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Europe"));
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("London"));
    }

    // ---- Error Handling Tests ----

    @Test
    void testExecuteWithNoResults() throws SQLException {
        String[] args = {"all-cities-continent", "InvalidContinent"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("No cities found"));
        assertTrue(output.contains("InvalidContinent"));
    }

    @Test
    void testExecuteWithSQLException() throws SQLException {
        String[] args = {"all-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database connection failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Database query failed"));
    }

    @Test
    void testExecuteWithPrepareStatementException() throws SQLException {
        String[] args = {"all-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Prepared statement creation failed"));
        
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    // ---- Query Parameter Tests ----

    @Test
    void testContinentParameterWithSpaces() throws SQLException {
        String[] args = {"all-cities-continent", "  North America  "};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should trim the continent name
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testContinentParameterCaseSensitive() throws SQLException {
        String[] args = {"all-cities-continent", "ASIA"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should pass the continent as provided
        verify(mockStatement).setString(1, "ASIA");
    }

    @Test
    void testMultipleResultsDisplayedCorrectly() throws SQLException {
        String[] args = {"all-cities-continent", "Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Mumbai", "Shanghai", "Beijing");
        when(mockResultSet.getString("Country")).thenReturn("India", "China", "China");
        when(mockResultSet.getString("District")).thenReturn("Maharashtra", "Shanghai", "Peking");
        when(mockResultSet.getLong("Population")).thenReturn(10_500_000L, 9_696_300L, 7_472_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Mumbai"));
        assertTrue(output.contains("Shanghai"));
        assertTrue(output.contains("Beijing"));
        assertTrue(output.contains("10,500,000"));
        assertTrue(output.contains("9,696,300"));
        assertTrue(output.contains("7,472,000"));
    }

    @Test
    void testMultiWordContinentName() throws SQLException {
        String[] args = {"all-cities-continent", "North", "America"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        command.execute(mockConnection, args);
        
        // Should join multi-word continent names
        verify(mockStatement).setString(1, "North America");
    }

    @Test
    void testPopulationSortingOrder() throws SQLException {
        String[] args = {"all-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Moscow", "London", "Berlin");
        when(mockResultSet.getString("Country")).thenReturn("Russia", "United Kingdom", "Germany");
        when(mockResultSet.getString("District")).thenReturn("Moscow", "England", "Berliini");
        when(mockResultSet.getLong("Population")).thenReturn(8_389_200L, 7_285_000L, 3_386_667L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        // Verify all cities are displayed
        assertTrue(output.contains("Moscow"));
        assertTrue(output.contains("London"));
        assertTrue(output.contains("Berlin"));
        // Verify populations are formatted correctly
        assertTrue(output.contains("8,389,200"));
        assertTrue(output.contains("7,285,000"));
        assertTrue(output.contains("3,386,667"));
    }

    @Test
    void testDatabaseConnectionError() throws SQLException {
        String[] args = {"all-cities-continent", "Africa"};
        
        when(mockConnection.prepareStatement(anyString()))
            .thenThrow(new SQLException("Connection lost"));
        
        SQLException exception = assertThrows(SQLException.class, 
            () -> command.execute(mockConnection, args));
        
        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void testQueryExecutionError() throws SQLException {
        String[] args = {"all-cities-continent", "Oceania"};
        
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
        String[] args = {"all-cities-continent", "Antarctica"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("TestCity");
        when(mockResultSet.getString("Country")).thenReturn("TestCountry");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getLong("Population")).thenReturn(100_000L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("TestCity"));
        assertTrue(output.contains("TestCountry"));
        assertTrue(output.contains("TestDistrict"));
        assertTrue(output.contains("100,000"));
    }

    @Test
    void testAllColumnsDisplayed() throws SQLException {
        String[] args = {"all-cities-continent", "Asia"};
        
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
        String[] args = {"all-cities-continent", "Asia"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Seoul");
        when(mockResultSet.getString("Country")).thenReturn("South Korea");
        when(mockResultSet.getString("District")).thenReturn("Seoul");
        when(mockResultSet.getLong("Population")).thenReturn(9_981_619L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("9,981,619"));
    }

    @Test
    void testCitiesWithSpecialCharacters() throws SQLException {
        String[] args = {"all-cities-continent", "Europe"};
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CityName")).thenReturn("Saint-Étienne");
        when(mockResultSet.getString("Country")).thenReturn("France");
        when(mockResultSet.getString("District")).thenReturn("Rhône-Alpes");
        when(mockResultSet.getLong("Population")).thenReturn(313_338L);
        
        command.execute(mockConnection, args);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Saint-Étienne"));
        assertTrue(output.contains("France"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
