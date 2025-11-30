package com.napier.sem.commands.district;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all cities in a specific district sorted by population.
 * User Story: As a Data Analyst, I want to view all cities in a specific district 
 * sorted by largest population to smallest so that I can focus on local population densities.
 */
public class AllCitiesByDistrictCommand extends CommandBase {

    public AllCitiesByDistrictCommand() {
        super("all-cities-district", "Display all cities in a district sorted by population (usage: all-cities-district <district>)");
    }

    /**
     * Retrieves and displays all cities in a specific district sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the district name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a district name. Usage: all-cities-district <district>");
            return;
        }

        // Join all arguments after the command name to support multi-word districts
        StringBuilder districtBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                districtBuilder.append(" ");
            }
            districtBuilder.append(args[i]);
        }
        String districtName = districtBuilder.toString().trim();

        if (districtName.isEmpty()) {
            System.out.println("  Invalid input. District name cannot be empty.");
            return;
        }

        // ---- SQL Query with District Parameter ----
        String sql = "SELECT " +
                "city.Name AS CityName, " +
                "country.Name AS Country, " +
                "city.District, " +
                "city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.District = ? " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, districtName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                String headerFormat = "%-30s %-30s %15s%n";
                
                System.out.println("\n All Cities in " + districtName + " (Sorted by Population)");
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
                    System.out.println("  No cities found for district: " + districtName);
                    System.out.println("  Please check the district name and try again.");
                }
                
                System.out.println(TableFormatter.generateSeparator(headerFormat) + "\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
