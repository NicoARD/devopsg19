package com.napier.sem.commands.global;

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
 * Unit tests for ViewGlobalLanguageDistributionCommand
 */
class ViewGlobalLanguageDistributionCommandTest {

    private ViewGlobalLanguageDistributionCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ViewGlobalLanguageDistributionCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("language-dist", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("percentage of people speaking"));
    }

    @Test
    @DisplayName("Should handle execute with valid connection")
    void testExecuteWithValidConnection() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(false);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"languagedist"});
        });
    }

    @Test
    @DisplayName("Should handle closed connection")
    void testExecuteWithClosedConnection() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(true);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"languagedist"});
        });
    }
}
