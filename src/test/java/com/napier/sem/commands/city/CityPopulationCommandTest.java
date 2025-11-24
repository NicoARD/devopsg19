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
 * Unit tests for CityPopulationCommand
 */
class CityPopulationCommandTest {

    private CityPopulationCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new CityPopulationCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("city-pop", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("city"));
    }

    @Test
    @DisplayName("Should handle execute with valid city")
    void testExecuteWithValidCity() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"citypop", "Tokyo"});
        });
    }

    @Test
    @DisplayName("Should handle execute with missing city")
    void testExecuteWithMissingCity() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"citypop"});
        });
    }

    @Test
    @DisplayName("Should handle execute with empty city")
    void testExecuteWithEmptyCity() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"citypop", ""});
        });
    }
}
