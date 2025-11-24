package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all capital cities in a specific region sorted by population.
 */
public class CapitalCitiesByRegionCommand extends CommandBase {

    public CapitalCitiesByRegionCommand() {
        super("capital-cities-region", "Display all capital cities in a region sorted by population (usage: capital-cities-region <region>)");
    }

    /**
     * Retrieves and displays all capital cities in a specific region sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the region name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a region name. Usage: capital-cities-region <region>");
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

        // ---- SQL Query ----
        String sql = "SELECT " +
                "city.Name AS CapitalCity, " +
                "country.Name AS Country, " +
                "city.Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n All Capital Cities in " + regionName + " (Sorted by Population)");
                System.out.println("==================================================================");
                System.out.printf("%-35s %-30s %15s%n", "Capital City", "Country", "Population");
                System.out.println("------------------------------------------------------------------");

                while (rs.next()) {
                    dataFound = true;
                    String capitalCity = rs.getString("CapitalCity");
                    String country = rs.getString("Country");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-30s %,15d%n", capitalCity, country, population);
                }

                if (!dataFound) {
                    System.out.println("  No capital cities found for region: " + regionName);
                    System.out.println("  Please check the region name and try again.");
                }
                
                System.out.println("==================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
