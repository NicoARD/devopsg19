package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all countries in a specific region sorted by population.
 * User Story: As a Data Analyst, I want to view all countries in a specific region 
 * sorted by largest population to smallest, so that I can compare populations within a region.
 */
public class AllCountriesByRegionCommand extends CommandBase {

    public AllCountriesByRegionCommand() {
        super("all-countries-region", "Display all countries in a region sorted by population (usage: all-countries-region <region>)");
    }

    /**
     * Retrieves and displays all countries in a specific region sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1+] is the region name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a region name. Usage: all-countries-region <region>");
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
                "Code, " +
                "Name, " +
                "Continent, " +
                "Region, " +
                "Population, " +
                "Capital " +
                "FROM country " +
                "WHERE Region = ? " +
                "ORDER BY Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n All Countries in " + regionName + " (Sorted by Population)");
                System.out.println("=======================================================================================================================");
                System.out.printf("%-5s %-45s %-20s %-30s %15s%n", "Code", "Country", "Continent", "Region", "Population");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------");

                while (rs.next()) {
                    dataFound = true;
                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String continent = rs.getString("Continent");
                    String region = rs.getString("Region");
                    long population = rs.getLong("Population");

                    System.out.printf("%-5s %-45s %-20s %-30s %,15d%n", 
                        code, name, continent, region, population);
                }

                if (!dataFound) {
                    System.out.println("  No countries found for region: " + regionName);
                    System.out.println("  Please check the region name and try again.");
                }
                
                System.out.println("=======================================================================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
