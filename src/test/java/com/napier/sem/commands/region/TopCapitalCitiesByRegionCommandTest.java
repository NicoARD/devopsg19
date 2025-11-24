package com.napier.sem.commands.region;

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

class TopCapitalCitiesByRegionCommandTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private TopCapitalCitiesByRegionCommand command;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        command = new TopCapitalCitiesByRegionCommand();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testInvalidArgs() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region"}));
    }

    @Test
    void testInvalidNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "Europe", "XYZ"}));
    }

    @Test
    void testNegativeNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "Asia", "-5"}));
    }

    @Test
    void testZeroNumber() {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "Asia", "0"}));
    }

    @Test
    void testValidInputDoesNotThrow() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("CapitalCity")).thenReturn("Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("Japan");
        when(mockResultSet.getInt("Population")).thenReturn(8336599);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "Eastern Asia", "5"}));

        verify(mockStatement).setString(1, "Eastern Asia");
        verify(mockStatement).setInt(2, 5);
    }

    @Test
    void testNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "UnknownRegion", "10"}));
    }

    @Test
    void testDatabaseError() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> command.execute(mockConnection, new String[]{"top-capital-cities-region", "Europe", "5"}));
    }
}
