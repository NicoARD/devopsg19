package com.napier.sem.commands.region;

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
 * Unit tests for ViewPopulationByRegionCommand
 */
class ViewPopulationByRegionCommandTest {

    private ViewPopulationByRegionCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ViewPopulationByRegionCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("regionpop", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("region"));
    }

    @Test
    @DisplayName("Should handle execute with valid region")
    void testExecuteWithValidRegion() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"regionpop", "Eastern Asia"});
        });
    }

    @Test
    @DisplayName("Should handle execute with missing region")
    void testExecuteWithMissingRegion() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"regionpop"});
        });
    }

    @Test
    @DisplayName("Should handle execute with empty region")
    void testExecuteWithEmptyRegion() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"regionpop", ""});
        });
    }
}
