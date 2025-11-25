package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all countries in a specific continent sorted by population.
 * User Story: As a Data Analyst, I want to view all countries in a specific continent 
 * sorted by largest population to smallest so that I can analyze population distribution in that continent.
 */
public class AllCountriesByContinentCommand extends CommandBase {

    public AllCountriesByContinentCommand() {
        super("all-countries-continent", "Display all countries in a continent sorted by population (usage: all-countries-continent <continent>)");
    }

    /**
     * Retrieves and displays all countries in a specific continent sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the continent name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a continent name. Usage: all-countries-continent <continent>");
            return;
        }

        // Join all arguments after the command name to support multi-word continents
        StringBuilder continentBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                continentBuilder.append(" ");
            }
            continentBuilder.append(args[i]);
        }
        String continentName = continentBuilder.toString().trim();

        if (continentName.isEmpty()) {
            System.out.println("  Invalid input. Continent name cannot be empty.");
            return;
        }

        // ---- SQL Query with Continent Parameter ----
        String sql = "SELECT " +
                "Code, " +
                "Name, " +
                "Continent, " +
                "Region, " +
                "Population, " +
                "Capital " +
                "FROM country " +
                "WHERE Continent = ? " +
                "ORDER BY Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, continentName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n All Countries in " + continentName + " (Sorted by Population)");
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
                    System.out.println("  No countries found for continent: " + continentName);
                    System.out.println("  Please check the continent name and try again.");
                }
                
                System.out.println("=======================================================================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
