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
 * Unit tests for TopCitiesByDistrictCommand
 */
class TopCitiesByDistrictCommandTest {

    private TopCitiesByDistrictCommand command;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new TopCitiesByDistrictCommand();
    }

    @Test
    @DisplayName("Should have proper execution command")
    void testExecutionCommand() {
        assertEquals("topcities-district", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should have proper command description")
    void testCommandDescription() {
        String description = command.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("district"));
    }

    @Test
    @DisplayName("Should handle execute with valid arguments")
    void testExecuteWithValidArgs() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcities-district", "California", "5"});
        });
    }

    @Test
    @DisplayName("Should handle execute with missing district")
    void testExecuteWithMissingDistrict() {
        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcities-district"});
        });
    }

    @Test
    @DisplayName("Should handle execute with default limit")
    void testExecuteWithDefaultLimit() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcities-district", "California"});
        });
    }

    @Test
    @DisplayName("Should handle invalid number format for limit")
    void testExecuteWithInvalidLimit() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> {
            command.execute(mockConnection, new String[]{"topcities-district", "California", "invalid"});
        });
    }
}
