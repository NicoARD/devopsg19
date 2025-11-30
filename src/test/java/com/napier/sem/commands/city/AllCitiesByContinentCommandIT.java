package com.napier.sem.commands.city;

import com.napier.sem.commands.continent.AllCitiesByContinentCommand;
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
 * Integration tests for AllCitiesByContinentCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByContinentCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByContinentCommand command;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCitiesByContinentCommand();
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void testExecuteWithValidContinent() throws Exception {
        // Given: Mock with Asian cities
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getString("Country")).thenReturn("China", "China");
        when(mockResultSet.getString("District")).thenReturn("Shanghai", "Beijing");
        when(mockResultSet.getInt("Population")).thenReturn(27058000, 21540000);

        // When: Execute command
        String[] args = {"all-cities-continent", "Asia"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify execution
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setString(1, "Asia");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordContinent() throws Exception {
        // Given: Mock with North American cities
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("New York");
        when(mockResultSet.getString("Country")).thenReturn("United States");
        when(mockResultSet.getString("District")).thenReturn("New York");
        when(mockResultSet.getInt("Population")).thenReturn(8419000);

        // When: Execute with multi-word continent
        String[] args = {"all-cities-continent", "North", "America"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify parameter was joined
        verify(mockPreparedStatement).setString(1, "North America");
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByContinent() throws Exception {
        // Given: Mock with European cities
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("London");
        when(mockResultSet.getString("Country")).thenReturn("United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("England");
        when(mockResultSet.getInt("Population")).thenReturn(8982000);

        // When: Execute command
        String[] args = {"all-cities-continent", "Europe"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Verify filtering
        verify(mockPreparedStatement).setString(1, "Europe");
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidContinent() throws Exception {
        // Given: Empty result set for invalid continent
        when(mockResultSet.next()).thenReturn(false);

        // When: Execute with invalid continent
        String[] args = {"all-cities-continent", "InvalidContinent"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        // Then: Should still execute query
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        // Given: Command with missing continent argument
        String[] args = {"all-cities-continent"};

        // When/Then: Should handle gracefully (prints usage)
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        // Given: Null connection
        Connection conn = null;
        
        // When/Then: Should throw exception
        String[] args = {"all-cities-continent", "Asia"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
