package com.napier.sem.commands.city;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TopCapitalCitiesCommand
 */
class TopCapitalCitiesCommandTest {

    private TopCapitalCitiesCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new TopCapitalCitiesCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("topcapitals", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("capital"));
    }

    @Test
    @DisplayName("Should handle execute with valid arguments")
    void testExecuteWithValidArgs() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcapitals", "10"});
        });
    }

    @Test
    @DisplayName("Should handle execute with default arguments")
    void testExecuteWithDefaultArgs() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcapitals"});
        });
    }

    @Test
    @DisplayName("Should handle invalid number format")
    void testExecuteWithInvalidNumber() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcapitals", "invalid"});
        });
    }
}
