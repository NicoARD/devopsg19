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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ViewPopulationByContinentCommand
 */
class ViewPopulationByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private ViewPopulationByContinentCommand command;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        command = new ViewPopulationByContinentCommand();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testMissingArguments() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"population-continent"}));
    }

    @Test
    void testEmptyContinent() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"population-continent", ""}));
    }

    @Test
    void testValidContinent() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000000L);
        when(mockResultSet.getLong("UrbanPopulation")).thenReturn(600000000L);
        when(mockResultSet.getLong("RuralPopulation")).thenReturn(400000000L);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"population-continent", "Asia"}));

        verify(mockStatement).setString(1, "Asia");
    }

    @Test
    void testNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"population-continent", "Antarctica"}));
    }

    @Test
    void testDatabaseError() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> command.execute(mockConnection, new String[]{"population-continent", "Europe"}));
    }
}
