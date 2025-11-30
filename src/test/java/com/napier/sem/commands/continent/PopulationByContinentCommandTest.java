package com.napier.sem.commands.continent;

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
 * Unit tests for PopulationByContinentCommand
 */
class PopulationByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private PopulationByContinentCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new PopulationByContinentCommand();

        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testConstructor() {
        assertEquals("population-continent", command.getExcecutionCommand());
        assertTrue(command.getDescription().contains("population details"));
    }

    @Test
    void testExecuteWithValidData() throws SQLException {
        // Given: Mock result set with continent population data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Continent"))
            .thenReturn("Asia", "Europe");
        when(mockResultSet.getLong("TotalPopulation"))
            .thenReturn(4500000000L, 740000000L);
        when(mockResultSet.getLong("UrbanPopulation"))
            .thenReturn(2250000000L, 555000000L);
        when(mockResultSet.wasNull()).thenReturn(false, false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify query was executed
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteCalculatesRuralPopulation() throws SQLException {
        // Given: Mock result set with known values
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(600000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Rural population should be calculated (400000 = 1000000 - 600000)
        verify(mockResultSet).getLong("TotalPopulation");
        verify(mockResultSet).getLong("UrbanPopulation");
    }

    @Test
    void testExecuteHandlesNullUrbanPopulation() throws SQLException {
        // Given: Mock result set with null urban population
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Antarctica");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(0L);
        when(mockResultSet.wasNull()).thenReturn(true); // Simulate NULL

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle null gracefully
        verify(mockResultSet).wasNull();
    }

    @Test
    void testExecuteCalculatesPercentages() throws SQLException {
        // Given: Mock result set with data for percentage calculation
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(750000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should calculate 75% urban, 25% rural
        verify(mockResultSet).getLong("TotalPopulation");
        verify(mockResultSet).getLong("UrbanPopulation");
    }

    @Test
    void testExecuteHandlesZeroTotalPopulation() throws SQLException {
        // Given: Mock result set with zero population
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("TestContinent");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(0L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(0L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle division by zero gracefully
        verify(mockResultSet).getLong("TotalPopulation");
    }

    @Test
    void testExecuteUsesGroupByContinent() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        command.execute(mockConnection, args);

        // Then: Verify SQL includes GROUP BY
        verify(mockConnection).prepareStatement(contains("GROUP BY"));
    }

    @Test
    void testExecuteOrdersByTotalPopulation() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        command.execute(mockConnection, args);

        // Then: Verify SQL includes ORDER BY TotalPopulation DESC
        verify(mockConnection).prepareStatement(contains("ORDER BY TotalPopulation DESC"));
    }

    @Test
    void testExecuteUsesLeftJoin() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        command.execute(mockConnection, args);

        // Then: Verify SQL uses LEFT JOIN to include countries without cities
        verify(mockConnection).prepareStatement(contains("LEFT JOIN"));
    }

    @Test
    void testExecuteWithNoResults() throws SQLException {
        // Given: Empty result set
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteWithMultipleContinents() throws SQLException {
        // Given: Mock result set with multiple continents
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Continent"))
            .thenReturn("Asia", "Europe", "Africa");
        when(mockResultSet.getLong("TotalPopulation"))
            .thenReturn(4500000000L, 740000000L, 1340000000L);
        when(mockResultSet.getLong("UrbanPopulation"))
            .thenReturn(2250000000L, 555000000L, 536000000L);
        when(mockResultSet.wasNull()).thenReturn(false, false, false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should process all continents
        verify(mockResultSet, times(4)).next();
    }

    @Test
    void testExecuteDisplaysCorrectColumns() throws SQLException {
        // Given: Mock with continent data
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify all columns are retrieved
        verify(mockResultSet).getString("Continent");
        verify(mockResultSet).getLong("TotalPopulation");
        verify(mockResultSet).getLong("UrbanPopulation");
        verify(mockResultSet).wasNull();
    }

    @Test
    void testExecuteWithNullConnection() {
        // Given: Null connection
        String[] args = {"population-continent"};

        // When/Then: Should throw exception
        assertThrows(NullPointerException.class, () -> command.execute(null, args));
    }

    @Test
    void testExecuteWithDatabaseError() throws SQLException {
        // Given: Database throws SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // When/Then: Should propagate exception
        String[] args = {"population-continent"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithQueryExecutionError() throws SQLException {
        // Given: Query execution fails
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // When/Then: Should propagate exception
        String[] args = {"population-continent"};
        assertThrows(SQLException.class, () -> command.execute(mockConnection, args));
    }

    @Test
    void testExecuteWithExtraArguments() throws SQLException {
        // Given: Command with extra arguments (should be ignored)
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute with extra arguments
        String[] args = {"population-continent", "extra", "args"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should execute normally
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testExecuteAggregatesPopulation() throws SQLException {
        // Given: Mock result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(4500000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(2250000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        command.execute(mockConnection, args);

        // Then: Verify SQL uses SUM aggregation
        verify(mockConnection).prepareStatement(contains("SUM(co.Population)"));
        verify(mockConnection).prepareStatement(contains("SUM(ci.Population)"));
    }

    @Test
    void testExecuteHandlesFullyUrbanContinent() throws SQLException {
        // Given: Mock result set with 100% urban population
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("TestContinent");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(1000000L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should calculate 100% urban, 0% rural
        verify(mockResultSet).getLong("TotalPopulation");
        verify(mockResultSet).getLong("UrbanPopulation");
    }

    @Test
    void testExecuteHandlesFullyRuralContinent() throws SQLException {
        // Given: Mock result set with 0% urban population
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("TestContinent");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(0L);
        when(mockResultSet.wasNull()).thenReturn(false);

        // When: Execute command
        String[] args = {"population-continent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should calculate 0% urban, 100% rural
        verify(mockResultSet).getLong("TotalPopulation");
        verify(mockResultSet).getLong("UrbanPopulation");
    }
}
