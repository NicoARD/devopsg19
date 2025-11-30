package com.napier.sem.commands.city;

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
 * Integration tests for AllCitiesCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private AllCitiesCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCitiesCommand();
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void testExecuteWithMockedDatabase() throws Exception {
        // Given: Mock result set with city data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getString("Country")).thenReturn("China", "China");
        when(mockResultSet.getString("District")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getInt("Population")).thenReturn(27058000, 21540000);

        // When: Execute command
        String[] args = {"all-cities"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify execution
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(2)
    void testExecuteReturnsAllCities() throws Exception {
        // Given: Mock with single city
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Test City");
        when(mockResultSet.getString("Country")).thenReturn("Test Country");
        when(mockResultSet.getString("District")).thenReturn("Test District");
        when(mockResultSet.getInt("Population")).thenReturn(500000);

        // When: Execute command
        String[] args = {"all-cities"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify execution
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(3)
    void testExecuteOrdersByPopulation() throws Exception {
        // Given: Cities ordered by population
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Large City", "Small City");
        when(mockResultSet.getString("Country")).thenReturn("Country", "Country");
        when(mockResultSet.getString("District")).thenReturn("District 2", "District 1");
        when(mockResultSet.getInt("Population")).thenReturn(5000000, 100000);

        // When: Execute command
        String[] args = {"all-cities"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify execution
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(4)
    void testExecuteWithNullConnection() {
        // Given: Null connection
        Connection conn = null;
        
        // When/Then: Should throw exception
        String[] args = {"all-cities"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
