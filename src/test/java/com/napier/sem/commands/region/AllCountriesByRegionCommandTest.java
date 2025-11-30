package com.napier.sem.commands.region;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AllCountriesByRegionCommand
 */
class AllCountriesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private AllCountriesByRegionCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCountriesByRegionCommand();

        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testConstructor() {
        assertEquals("all-countries-region", command.getCommandName());
        assertTrue(command.getDescription().contains("region"));
    }

    @Test
    void testExecuteWithValidRegion() throws SQLException {
        // Given: Mock result set with country data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("CHN", "JPN");
        when(mockResultSet.getString("Name")).thenReturn("China", "Japan");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Asia");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Asia", "Eastern Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1440000000L, 126000000L);

        // When: Execute command with single-word region
        String[] args = {"all-countries-region", "Eastern Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify query was executed with correct parameter
        verify(mockPreparedStatement).setString(1, "Eastern Asia");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithMultiWordRegion() throws SQLException {
        // Given: Mock result set with country data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute with multi-word region
        String[] args = {"all-countries-region", "Western", "Europe"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify region name was joined correctly
        verify(mockPreparedStatement).setString(1, "Western Europe");
    }

    @Test
    void testExecuteOrdersByPopulationDescending() throws SQLException {
        // Given: Mock result set with countries in descending population order
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("USA", "CAN", "MEX");
        when(mockResultSet.getString("Name")).thenReturn("United States", "Canada", "Mexico");
        when(mockResultSet.getString("Continent")).thenReturn("North America", "North America", "North America");
        when(mockResultSet.getString("Region")).thenReturn("North America", "North America", "North America");
        when(mockResultSet.getLong("Population")).thenReturn(331000000L, 38000000L, 128000000L);

        // When: Execute command
        String[] args = {"all-countries-region", "North America"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify SQL includes ORDER BY Population DESC
        verify(mockConnection).prepareStatement(contains("ORDER BY Population DESC"));
    }

    @Test
    void testExecuteWithNoResults() throws SQLException {
        // Given: Empty result set
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute with invalid region
        String[] args = {"all-countries-region", "InvalidRegion"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithMissingArguments() throws SQLException {
        // Given: Command without region argument
        String[] args = {"all-countries-region"};

        // When/Then: Should handle gracefully without querying database
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteWithEmptyRegionName() throws SQLException {
        // Given: Command with empty region
        String[] args = {"all-countries-region", ""};

        // When/Then: Should handle gracefully
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteDisplaysCorrectColumns() throws SQLException {
        // Given: Mock with country data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("GBR");
        when(mockResultSet.getString("Name")).thenReturn("United Kingdom");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("British Islands");
        when(mockResultSet.getLong("Population")).thenReturn(68000000L);

        // When: Execute command
        String[] args = {"all-countries-region", "British Islands"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify all columns are retrieved
        verify(mockResultSet).getString("Code");
        verify(mockResultSet).getString("Name");
        verify(mockResultSet).getString("Continent");
        verify(mockResultSet).getString("Region");
        verify(mockResultSet).getLong("Population");
    }

    @Test
    void testExecuteWithNullConnection() {
        // Given: Null connection
        String[] args = {"all-countries-region", "Eastern Asia"};

        // When/Then: Should throw exception
        assertThrows(NullPointerException.class, () -> command.execute(null, args));
    }

    @Test
    void testExecuteWithDatabaseError() throws SQLException {
        // Given: Database throws SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // When/Then: Should propagate exception
        String[] args = {"all-countries-region", "Eastern Asia"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithQueryExecutionError() throws SQLException {
        // Given: Query execution fails
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // When/Then: Should propagate exception
        String[] args = {"all-countries-region", "Eastern Asia"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteFiltersCorrectly() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("FRA");
        when(mockResultSet.getString("Name")).thenReturn("France");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(67000000L);

        // When: Execute command
        String[] args = {"all-countries-region", "Western Europe"};
        command.execute(mockConnection, args);

        // Then: Verify WHERE clause is used
        verify(mockConnection).prepareStatement(contains("WHERE Region = ?"));
        verify(mockPreparedStatement).setString(1, "Western Europe");
    }

    @Test
    void testExecuteWithSpecialCharactersInRegion() throws SQLException {
        // Given: Region name with special characters
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute with special characters
        String[] args = {"all-countries-region", "South-East Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle correctly
        verify(mockPreparedStatement).setString(1, "South-East Asia");
    }
}
