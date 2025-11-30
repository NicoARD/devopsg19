package com.napier.sem.commands.city;

import com.napier.sem.commands.region.TopCitiesByRegionCommand;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests for TopCitiesByRegionCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TopCitiesByRegionCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private TopCitiesByRegionCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new TopCitiesByRegionCommand();
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void testExecuteWithValidRegionAndLimit() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getString("Country")).thenReturn("China", "China");
        when(mockResultSet.getString("District")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getInt("Population")).thenReturn(27058000, 21540000);

        String[] args = {"top-cities-region", "Eastern Asia", "5"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "Eastern Asia");
        verify(mockPreparedStatement).setInt(2, 5);
    }

    @Test
    @Order(2)
    void testExecuteWithSingleWordRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Test City");
        when(mockResultSet.getString("Country")).thenReturn("Test Country");
        when(mockResultSet.getString("District")).thenReturn("Test District");
        when(mockResultSet.getInt("Population")).thenReturn(1000000);

        String[] args = {"top-cities-region", "TestRegion", "3"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "TestRegion");
        verify(mockPreparedStatement).setInt(2, 3);
    }

    @Test
    @Order(3)
    void testExecuteRespectsLimit() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Tokyo", "Osaka");
        when(mockResultSet.getString("Country")).thenReturn("Japan", "Japan");
        when(mockResultSet.getString("District")).thenReturn("Tokyo", "Osaka");
        when(mockResultSet.getInt("Population")).thenReturn(13960000, 19281000);

        String[] args = {"top-cities-region", "Eastern Asia", "2"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setInt(2, 2);
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidLimit() throws Exception {
        String[] args = {"top-cities-region", "Eastern Asia", "invalid"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(5)
    void testExecuteWithNegativeLimit() throws Exception {
        String[] args = {"top-cities-region", "Eastern Asia", "-5"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(6)
    void testExecuteWithZeroLimit() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String[] args = {"top-cities-region", "Eastern Asia", "0"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(7)
    void testExecuteWithLimitGreaterThanAvailable() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String[] args = {"top-cities-region", "Eastern Asia", "1000"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(8)
    void testExecuteWithMissingArguments() throws Exception {
        String[] args = {"top-cities-region", "Eastern Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(9)
    void testExecuteWithInvalidRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String[] args = {"top-cities-region", "InvalidRegion", "5"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(10)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"top-cities-region", "Eastern Asia", "5"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
