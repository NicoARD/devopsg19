package com.napier.sem.commands.continent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TopCapitalCitiesByContinentCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private TopCapitalCitiesByContinentCommand command;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        command = new TopCapitalCitiesByContinentCommand();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testMissingArguments() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent"}));
    }

    @Test
    void testInvalidNumberInput() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Asia", "abc"}));
    }

    @Test
    void testNegativeNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Europe", "-5"}));
    }

    @Test
    void testZeroNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Europe", "0"}));
    }

    @Test
    void testValidInput() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("Japan");
        when(mockResultSet.getInt("Population")).thenReturn(8336599);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Asia", "5"}));

        verify(mockStatement).setString(1, "Asia");
        verify(mockStatement).setInt(2, 5);
    }

    @Test
    void testNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Antarctica", "10"}));
    }

    @Test
    void testDatabaseError() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> command.execute(mockConnection, new String[]{"top-capital-cities-continent", "Europe", "5"}));
    }
}
