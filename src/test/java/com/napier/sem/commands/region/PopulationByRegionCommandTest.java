package com.napier.sem.commands.region;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// Corrected import: since the class is in the same package, no import needed
class PopulationByRegionCommandTest {

    private PopulationByRegionCommand command;

    @Mock
    private Connection conn;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        command = new PopulationByRegionCommand();
    }

    @Test
    void testMissingArgs() {
        assertDoesNotThrow(() -> command.execute(conn, new String[]{}));
    }

    @Test
    void testEmptyRegion() {
        assertDoesNotThrow(() -> command.execute(conn, new String[]{""}));
    }

    @Test
    void testValidRegion() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertDoesNotThrow(() -> command.execute(conn, new String[]{"Western Europe"}));
    }
}
