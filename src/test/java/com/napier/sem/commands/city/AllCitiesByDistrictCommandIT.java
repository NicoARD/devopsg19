package com.napier.sem.commands.city;

import com.napier.sem.commands.district.AllCitiesByDistrictCommand;
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
 * Integration tests for AllCitiesByDistrictCommand
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllCitiesByDistrictCommandIT {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    private AllCitiesByDistrictCommand command;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        command = new AllCitiesByDistrictCommand();
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    void testExecuteWithValidDistrict() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("London", "Manchester");
        when(mockResultSet.getString("Country")).thenReturn("United Kingdom", "United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("England", "England");
        when(mockResultSet.getInt("Population")).thenReturn(8982000, 547000);

        String[] args = {"all-cities-district", "England"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "England");
    }

    @Test
    @Order(2)
    void testExecuteWithMultiWordDistrict() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Test City");
        when(mockResultSet.getString("Country")).thenReturn("Test Country");
        when(mockResultSet.getString("District")).thenReturn("Multi Word District");
        when(mockResultSet.getInt("Population")).thenReturn(500000);

        String[] args = {"all-cities-district", "Multi", "Word", "District"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).setString(1, "Multi Word District");
    }

    @Test
    @Order(3)
    void testExecuteFiltersCitiesByDistrict() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("London");
        when(mockResultSet.getString("Country")).thenReturn("United Kingdom");
        when(mockResultSet.getString("District")).thenReturn("England");
        when(mockResultSet.getInt("Population")).thenReturn(8982000);

        String[] args = {"all-cities-district", "England"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(4)
    void testExecuteWithInvalidDistrict() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String[] args = {"all-cities-district", "InvalidDistrict"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @Order(5)
    void testExecuteWithMissingArguments() throws Exception {
        String[] args = {"all-cities-district"};
        assertDoesNotThrow(() -> command.execute(mockConnection, args));
    }

    @Test
    @Order(6)
    void testExecuteWithNullConnection() {
        Connection conn = null;
        String[] args = {"all-cities-district", "England"};
        assertThrows(Exception.class, () -> command.execute(conn, args));
    }
}
