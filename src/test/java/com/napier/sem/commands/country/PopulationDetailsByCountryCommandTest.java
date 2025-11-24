package com.napier.sem.commands.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PopulationDetailsByCountryCommandTest {

    private PopulationDetailsByCountryCommand command;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new PopulationDetailsByCountryCommand() {
            @Override
            public String getExcecutionCommand() {
                return "";
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public void execute(Connection connection, String[] args) throws SQLException {

            }
        };
    }

    @Test
    @DisplayName("Should execute with missing argument without throwing")
    void testExecuteWithMissingArgument() throws SQLException {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{}));
    }

    @Test
    @DisplayName("Should execute with empty country without throwing")
    void testExecuteWithEmptyCountry() throws SQLException {
        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{""}));
    }

    @Test
    @DisplayName("Should execute with valid country without throwing")
    void testExecuteWithValidCountry() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(mockConnection, new String[]{"France"}));
    }
}
