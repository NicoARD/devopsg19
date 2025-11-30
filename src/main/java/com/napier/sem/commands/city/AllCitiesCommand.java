package com.napier.sem.commands.city;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all cities in the world sorted by population.
 * User Story: As a Data Analyst, I want to view all cities in the world 
 * sorted by largest population to smallest so that I can understand global urban population distribution.
 */
public class AllCitiesCommand extends CommandBase {

    public AllCitiesCommand() {
        super("all-cities", "Display all cities in the world sorted by population (usage: all-cities)");
    }

    /**
     * Retrieves and displays all cities in the world sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments (no additional arguments required)
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- SQL Query to retrieve all cities ----
        String sql = "SELECT " +
                "city.Name AS CityName, " +
                "country.Name AS Country, " +
                "city.District, " +
                "city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            boolean dataFound = false;
            
            System.out.println("\nAll Cities in the World (Sorted by Population)");
            System.out.println("==================================================================");
            System.out.printf("%-35s %-30s %-20s %15s%n", "City", "Country", "District", "Population");
            System.out.println("------------------------------------------------------------------");

            while (rs.next()) {
                dataFound = true;
                String cityName = rs.getString("CityName");
                String country = rs.getString("Country");
                String district = rs.getString("District");
                long population = rs.getLong("Population");

                System.out.printf("%-35s %-30s %-20s %,15d%n", cityName, country, district, population);
            }

            if (!dataFound) {
                System.out.println("  No cities found in the database.");
            }
            
            System.out.println("==================================================================\n");
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
