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
 * Unit tests for TopCountriesByRegionCommand
 */
class TopCountriesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private TopCountriesByRegionCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new TopCountriesByRegionCommand();

        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testConstructor() {
        assertEquals("top-countries-region", command.getCommandName());
        assertTrue(command.getDescription().contains("top N countries"));
    }

    @Test
    void testExecuteWithValidRegionAndLimit() throws SQLException {
        // Given: Mock result set with country data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU", "FRA");
        when(mockResultSet.getString("Name")).thenReturn("Germany", "France");
        when(mockResultSet.getString("Continent")).thenReturn("Europe", "Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe", "Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L, 67000000L);

        // When: Execute command
        String[] args = {"top-countries-region", "Western", "Europe", "5"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify query was executed with correct parameters
        verify(mockPreparedStatement).setString(1, "Western Europe");
        verify(mockPreparedStatement).setInt(2, 5);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithSingleWordRegion() throws SQLException {
        // Given: Mock result set with country data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("ETH");
        when(mockResultSet.getString("Name")).thenReturn("Ethiopia");
        when(mockResultSet.getString("Continent")).thenReturn("Africa");
        when(mockResultSet.getString("Region")).thenReturn("Eastern Africa");
        when(mockResultSet.getLong("Population")).thenReturn(115000000L);

        // When: Execute with single word region
        String[] args = {"top-countries-region", "Micronesia", "3"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify region name was used correctly
        verify(mockPreparedStatement).setString(1, "Micronesia");
        verify(mockPreparedStatement).setInt(2, 3);
    }

    @Test
    void testExecuteWithMultiWordRegion() throws SQLException {
        // Given: Mock result set with country data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("IND");
        when(mockResultSet.getString("Name")).thenReturn("India");
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getString("Region")).thenReturn("Southern and Central Asia");
        when(mockResultSet.getLong("Population")).thenReturn(1380000000L);

        // When: Execute with multi-word region
        String[] args = {"top-countries-region", "Southern", "and", "Central", "Asia", "3"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify region name was joined correctly
        verify(mockPreparedStatement).setString(1, "Southern and Central Asia");
        verify(mockPreparedStatement).setInt(2, 3);
    }

    @Test
    void testExecuteWithLimitOne() throws SQLException {
        // Given: Mock result set with one country
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute with N=1
        String[] args = {"top-countries-region", "Western", "Europe", "1"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify limit is 1
        verify(mockPreparedStatement).setInt(2, 1);
    }

    @Test
    void testExecuteOrdersByPopulationDescending() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute command
        String[] args = {"top-countries-region", "Western", "Europe", "10"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify SQL includes ORDER BY Population DESC
        verify(mockConnection).prepareStatement(contains("ORDER BY Population DESC"));
    }

    @Test
    void testExecuteWithNoResults() throws SQLException {
        // Given: Empty result set
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute with invalid region
        String[] args = {"top-countries-region", "InvalidRegion", "5"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithMissingArguments() throws SQLException {
        // Given: Command without enough arguments
        String[] args = {"top-countries-region", "Europe"};

        // When/Then: Should handle gracefully without querying database
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteWithInvalidN() throws SQLException {
        // Given: Command with invalid N
        String[] args = {"top-countries-region", "Western", "Europe", "invalid"};

        // When/Then: Should handle gracefully
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteWithNegativeN() throws SQLException {
        // Given: Command with negative N
        String[] args = {"top-countries-region", "Western", "Europe", "-5"};

        // When/Then: Should handle gracefully
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteWithZeroN() throws SQLException {
        // Given: Command with zero N
        String[] args = {"top-countries-region", "Western", "Europe", "0"};

        // When/Then: Should handle gracefully
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testExecuteUsesLimit() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute command
        String[] args = {"top-countries-region", "Western", "Europe", "10"};
        command.execute(mockConnection, args);

        // Then: Verify LIMIT clause is used
        verify(mockConnection).prepareStatement(contains("LIMIT ?"));
        verify(mockPreparedStatement).setInt(2, 10);
    }

    @Test
    void testExecuteDisplaysCorrectColumns() throws SQLException {
        // Given: Mock with country data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute command
        String[] args = {"top-countries-region", "Western", "Europe", "5"};
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
        String[] args = {"top-countries-region", "Western", "Europe", "5"};

        // When/Then: Should throw exception
        assertThrows(NullPointerException.class, () -> command.execute(null, args));
    }

    @Test
    void testExecuteWithDatabaseError() throws SQLException {
        // Given: Database throws SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // When/Then: Should propagate exception
        String[] args = {"top-countries-region", "Western", "Europe", "5"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithQueryExecutionError() throws SQLException {
        // Given: Query execution fails
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // When/Then: Should propagate exception
        String[] args = {"top-countries-region", "Western", "Europe", "5"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteFiltersCorrectly() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("DEU");
        when(mockResultSet.getString("Name")).thenReturn("Germany");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Western Europe");
        when(mockResultSet.getLong("Population")).thenReturn(83000000L);

        // When: Execute command
        String[] args = {"top-countries-region", "Western", "Europe", "10"};
        command.execute(mockConnection, args);

        // Then: Verify WHERE clause is used
        verify(mockConnection).prepareStatement(contains("WHERE Region = ?"));
        verify(mockPreparedStatement).setString(1, "Western Europe");
    }

    @Test
    void testExecuteWithLargeN() throws SQLException {
        // Given: Large N value
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute with large N
        String[] args = {"top-countries-region", "Western", "Europe", "1000"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).setInt(2, 1000);
    }
}
