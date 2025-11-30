package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all cities in a specific region sorted by population.
 * User Story: As a Data Analyst, I want to view all cities in a specific region 
 * sorted by largest population to smallest so that I can compare city populations within a region.
 */
public class AllCitiesByRegionCommand extends CommandBase {

    public AllCitiesByRegionCommand() {
        super("all-cities-region", "Display all cities in a region sorted by population (usage: all-cities-region <region>)");
    }

    /**
     * Retrieves and displays all cities in a specific region sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the region name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a region name. Usage: all-cities-region <region>");
            return;
        }

        // Join all arguments after the command name to support multi-word regions
        StringBuilder regionBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                regionBuilder.append(" ");
            }
            regionBuilder.append(args[i]);
        }
        String regionName = regionBuilder.toString().trim();

        if (regionName.isEmpty()) {
            System.out.println("  Invalid input. Region name cannot be empty.");
            return;
        }

        // ---- SQL Query with Region Parameter ----
        String sql = "SELECT " +
                "city.Name AS CityName, " +
                "country.Name AS Country, " +
                "city.District, " +
                "city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                String headerFormat = "%-30s %-30s %15s%n";
                
                System.out.println("\n All Cities in " + regionName + " (Sorted by Population)");
                System.out.println(TableFormatter.generateSeparator(headerFormat));
                System.out.printf(headerFormat, "City", "Country", "Population");
                System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

                while (rs.next()) {
                    dataFound = true;
                    String cityName = rs.getString("CityName");
                    String country = rs.getString("Country");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-30s %-20s %,15d%n", cityName, country, district, population);
                }

                if (!dataFound) {
                    System.out.println("  No cities found for region: " + regionName);
                    System.out.println("  Please check the region name and try again.");
                }
                
                System.out.println(TableFormatter.generateSeparator(headerFormat) + "\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
