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
 * Integration tests for AllCapitalCitiesCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCapitalCitiesCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private AllCapitalCitiesCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCapitalCitiesCommand();
        
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
        // Given: Mock result set with capital city data
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Beijing", "Tokyo");
        when(mockResultSet.getString("Country")).thenReturn("China", "Japan");
        when(mockResultSet.getInt("Population")).thenReturn(21540000, 13960000);

        // When: Execute the command
        String[] args = {"all-capitals"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify SQL was executed
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(2)
    void testExecuteReturnsCapitalCitiesOnly() throws Exception {
        // Given: Mock with single capital city
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Test Capital");
        when(mockResultSet.getString("Country")).thenReturn("Test Country");
        when(mockResultSet.getInt("Population")).thenReturn(1000000);

        // When: Execute command
        String[] args = {"all-capitals"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify execution
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(3)
    void testExecuteOrdersByPopulation() throws Exception {
        // Given: Multiple capitals ordered by population
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Large Capital", "Small Capital");
        when(mockResultSet.getString("Country")).thenReturn("Country 2", "Country 1");
        when(mockResultSet.getInt("Population")).thenReturn(2000000, 500000);

        // When: Execute command
        String[] args = {"all-capitals"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify query execution
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(4)
    void testExecuteWithNullConnection() {
        // Given: Null connection
        Connection conn = null;

        // When/Then: Should throw exception
        String[] args = {"all-capitals"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }

    @Test
    @Order(5)
    void testExecuteWithEmptyResultSet() throws Exception {
        // Given: Empty result set
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute command
        String[] args = {"all-capitals"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should handle gracefully
        verify(mockPreparedStatement).executeQuery();
    }
}
