package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve the top N populated countries in a specific region.
 * User Story: As a Data Analyst, I want to view the top N populated countries in a specific region 
 * so that I can focus on key countries in that region.
 */
public class TopCountriesByRegionCommand extends CommandBase {

    public TopCountriesByRegionCommand() {
        super("top-countries-region", "Display top N countries in a region by population (usage: top-countries-region <region> <N>)");
    }

    /**
     * Retrieves and displays the top N most populated countries in a specific region.
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1...n-1] is the region name and args[n] is N
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 3) {
            System.out.println("  Please provide region and number. Usage: top-countries-region <region> <N>");
            System.out.println("  Example: top-countries-region Western Europe 10");
            return;
        }

        // Parse N (last argument)
        int n;
        try {
            n = Integer.parseInt(args[args.length - 1]);
            if (n <= 0) {
                System.out.println("  Invalid input. N must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input. N must be a valid number.");
            return;
        }

        // Join arguments between command and N as region name (supports multi-word regions)
        StringBuilder regionBuilder = new StringBuilder();
        for (int i = 1; i < args.length - 1; i++) {
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

        // ---- SQL Query with Region Filter and Limit ----
        String sql = "SELECT " +
                "Code, " +
                "Name, " +
                "Continent, " +
                "Region, " +
                "Population, " +
                "Capital " +
                "FROM country " +
                "WHERE Region = ? " +
                "ORDER BY Population DESC " +
                "LIMIT ?";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionName);
            stmt.setInt(2, n);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n Top " + n + " Countries in " + regionName + " (Sorted by Population)");
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
