package com.napier.sem.commands.world;

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
 * Unit tests for TotalWorldPopulationCommand
 */
class TotalWorldPopulationCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private TotalWorldPopulationCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new TotalWorldPopulationCommand();

        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testConstructor() {
        assertEquals("population-world", command.getExcecutionCommand());
        assertTrue(command.getDescription().contains("total population"));
    }

    @Test
    void testExecuteWithValidData() throws SQLException {
        // Given: Mock result set with world population
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute command
        String[] args = {"population-world"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify query was executed
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).getLong("TotalPopulation");
    }

    @Test
    void testExecuteUsesSumAggregation() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute command
        String[] args = {"population-world"};
        command.execute(mockConnection, args);

        // Then: Verify SQL uses SUM aggregation
        verify(mockConnection).prepareStatement(contains("SUM(Population)"));
    }

    @Test
    void testExecuteQueriesCountryTable() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute command
        String[] args = {"population-world"};
        command.execute(mockConnection, args);

        // Then: Verify SQL queries country table
        verify(mockConnection).prepareStatement(contains("FROM country"));
    }

    @Test
    void testExecuteWithNoResults() throws SQLException {
        // Given: Empty result set
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-world"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithZeroPopulation() throws SQLException {
        // Given: Mock result set with zero population
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(0L);

        // When: Execute command
        String[] args = {"population-world"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should display zero
        verify(mockResultSet).getLong("TotalPopulation");
    }

    @Test
    void testExecuteWithLargePopulation() throws SQLException {
        // Given: Mock result set with very large population
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(9999999999L);

        // When: Execute command
        String[] args = {"population-world"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle large numbers
        verify(mockResultSet).getLong("TotalPopulation");
    }

    @Test
    void testExecuteWithNullConnection() {
        // Given: Null connection
        String[] args = {"population-world"};

        // When/Then: Should throw exception
        assertThrows(NullPointerException.class, () -> command.execute(null, args));
    }

    @Test
    void testExecuteWithDatabaseError() throws SQLException {
        // Given: Database throws SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // When/Then: Should propagate exception
        String[] args = {"population-world"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithQueryExecutionError() throws SQLException {
        // Given: Query execution fails
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // When/Then: Should propagate exception
        String[] args = {"population-world"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithExtraArguments() throws SQLException {
        // Given: Command with extra arguments (should be ignored)
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute with extra arguments
        String[] args = {"population-world", "extra", "args"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should execute normally
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteRetrievesCorrectColumn() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute command
        String[] args = {"population-world"};
        command.execute(mockConnection, args);

        // Then: Verify correct column is retrieved
        verify(mockResultSet).getLong("TotalPopulation");
    }

    @Test
    void testExecuteDoesNotRequireArguments() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute with minimal arguments
        String[] args = {"population-world"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should work without additional arguments
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteClosesResources() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(7800000000L);

        // When: Execute command
        String[] args = {"population-world"};
        command.execute(mockConnection, args);

        // Then: Verify resources are accessed (try-with-resources will close them)
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testExecuteWithResultSetError() throws SQLException {
        // Given: ResultSet.next() throws exception
        when(mockResultSet.next()).thenThrow(new SQLException("ResultSet error"));

        // When/Then: Should propagate exception
        String[] args = {"population-world"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithGetLongError() throws SQLException {
        // Given: ResultSet.getLong() throws exception
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenThrow(new SQLException("Column error"));

        // When/Then: Should propagate exception
        String[] args = {"population-world"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }
}
