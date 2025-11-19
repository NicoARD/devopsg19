package com.napier.sem.commands.district;

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
 * Unit tests for ViewPopulationByDistrictCommand
 */
class ViewPopulationByDistrictCommandTest {

    private ViewPopulationByDistrictCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ViewPopulationByDistrictCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("districtpop", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("district"));
    }

    @Test
    @DisplayName("Should handle execute with valid district")
    void testExecuteWithValidDistrict() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(1000000L);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"districtpop", "California"});
        });
    }

    @Test
    @DisplayName("Should handle execute with missing district")
    void testExecuteWithMissingDistrict() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"districtpop"});
        });
    }

    @Test
    @DisplayName("Should handle execute with empty district")
    void testExecuteWithEmptyDistrict() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"districtpop", ""});
        });
    }
}
