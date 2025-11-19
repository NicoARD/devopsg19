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
 * Unit tests for CountryPopulationCommand
 */
class CountryPopulationCommandTest {

    private CountryPopulationCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new CountryPopulationCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("countrypop", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("country"));
    }

    @Test
    @DisplayName("Should handle execute with valid country")
    void testExecuteWithValidCountry() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"countrypop", "China"});
        });
    }

    @Test
    @DisplayName("Should handle execute with missing country")
    void testExecuteWithMissingCountry() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"countrypop"});
        });
    }

    @Test
    @DisplayName("Should handle execute with empty country")
    void testExecuteWithEmptyCountry() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"countrypop", ""});
        });
    }
}
