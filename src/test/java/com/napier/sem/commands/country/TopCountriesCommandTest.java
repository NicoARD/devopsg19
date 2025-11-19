package com.napier.sem.commands.country;

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
 * Unit tests for TopCountriesCommand
 */
class TopCountriesCommandTest {

    private TopCountriesCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new TopCountriesCommand();
    }


    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description, "Command description should not be null");
        assertFalse(description.trim().isEmpty(), "Command description should not be empty");
        assertTrue(description.contains("countries"), "Description should mention countries");
        assertTrue(description.contains("population"), "Description should mention population");
    }

    @Test
    @DisplayName("Should handle execute method with mock connection")
    void testExecuteMethod() throws SQLException {
        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        // Test that execute method can be called without throwing exceptions
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcountries", "5"});
        }, "Execute method should handle arguments gracefully");
    }

    @Test
    @DisplayName("Should handle execute with default arguments")
    void testExecuteWithDefaultArgs() throws SQLException {
        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcountries"});
        }, "Execute method should handle missing count argument");
    }

    @Test
    @DisplayName("Should handle invalid number format")
    void testExecuteWithInvalidNumber() throws SQLException {
        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcountries", "invalid"});
        }, "Execute method should handle invalid number format");
    }

}