package com.napier.sem.commands.city;

import com.napier.sem.commands.region.AllCitiesByRegionCommand;
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
 * Integration tests for AllCitiesByRegionCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByRegionCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByRegionCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCitiesByRegionCommand();
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void testExecuteWithValidRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getString("Country")).thenReturn("China", "China");
        when(mockResultSet.getString("District")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getInt("Population")).thenReturn(27058000, 21540000);

        String[] args = {"all-cities-region", "Eastern Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "Eastern Asia");
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Berlin");
        when(mockResultSet.getString("Country")).thenReturn("Germany");
        when(mockResultSet.getString("District")).thenReturn("Berlin");
        when(mockResultSet.getInt("Population")).thenReturn(3645000);

        String[] args = {"all-cities-region", "Western", "Europe"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "Western Europe");
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("Japan");
        when(mockResultSet.getString("District")).thenReturn("Tokyo");
        when(mockResultSet.getInt("Population")).thenReturn(13960000);

        String[] args = {"all-cities-region", "Eastern Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidRegion() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String[] args = {"all-cities-region", "InvalidRegion"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        String[] args = {"all-cities-region"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"all-cities-region", "Eastern Asia"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
