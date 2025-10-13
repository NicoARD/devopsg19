package com.napier.sem;

import java.sql.*;

/**
 * Database connectivity test and utility class
 */
public class DatabaseTest {
    
    /**
     * Test database connectivity with detailed output
     * @return true if connection successful, false otherwise
     */
    public static boolean testDatabaseConnection() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("✅ Database connection established successfully!");
            
            // Test basic query execution
            return performPingTest(connection);
            
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("💥 Error: " + e.getMessage());
            System.err.println("🔍 SQL State: " + e.getSQLState());
            System.err.println("🔢 Error Code: " + e.getErrorCode());
            return false;
        }
    }
    
    /**
     * Perform a comprehensive ping test with database information
     * @param connection Active database connection
     * @return true if all tests pass
     */
    private static boolean performPingTest(Connection connection) {
        try {
            System.out.println("\n📊 Database Information:");
            
            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("Connection URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            
            // Test query execution - check if world database is accessible

            String query = "SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_schema = 'world'";
            
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int tableCount = rs.getInt("table_count");
                    System.out.println("Tables found in 'world' database: " + tableCount);
                    
                    if (tableCount > 0) {
                        // Get specific table information
                        getTableInformation(connection);
                        
                        // Test sample data query
                        testSampleDataQuery(connection);
                        
                        System.out.println("✅ Database ping test completed successfully!");
                        return true;
                    } else {
                        System.out.println("⚠️  Warning: No tables found in 'world' database");
                        return false;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database ping test failed!");
            System.err.println("Error: " + e.getMessage());
            return false;
        }
        
        return false;
    }
    
    /**
     * Get information about tables in the world database
     * @param connection Active database connection
     */
    private static void getTableInformation(Connection connection) throws SQLException {
        String tableQuery = "SELECT table_name, table_rows FROM information_schema.tables " +
                           "WHERE table_schema = 'world' ORDER BY table_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(tableQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n📋 World Database Tables:");
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                long tableRows = rs.getLong("table_rows");
                System.out.println("  - " + tableName + " (" + tableRows + " rows)");
            }
        }
    }
    
    /**
     * Test a sample data query from the world database
     * @param connection Active database connection
     */
    private static void testSampleDataQuery(Connection connection) throws SQLException {
        String sampleQuery = "SELECT Name, Population, Continent FROM country ORDER BY Population DESC LIMIT 5";
        
        try (PreparedStatement stmt = connection.prepareStatement(sampleQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n🏆 Top 5 Countries by Population:");
            while (rs.next()) {
                String name = rs.getString("Name");
                long population = rs.getLong("Population");
                String continent = rs.getString("Continent");
                System.out.printf("  %s (%s) - %,d people%n", name, continent, population);
            }
        } catch (SQLException e) {
            // If country table doesn't exist, that's okay - just note it
            System.out.println(e.getMessage());
        }
    }
}