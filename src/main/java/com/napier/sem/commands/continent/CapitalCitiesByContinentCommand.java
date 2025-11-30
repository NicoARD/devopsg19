package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all capital cities in a specific continent sorted by population.
 * User Story: As a Data Analyst, I want to view all capital cities in a specific continent 
 * sorted by largest population to smallest so that I can analyze capitals in that continent.
 */
public class CapitalCitiesByContinentCommand extends CommandBase {

    public CapitalCitiesByContinentCommand() {
        super("capital-cities-continent", "Display all capital cities in a continent sorted by population (usage: capital-cities-continent <continent>)");
    }

    /**
     * Retrieves and displays all capital cities in a specific continent sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the continent name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a continent name. Usage: capital-cities-continent <continent>");
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
                "city.Name AS CapitalCity, " +
                "country.Name AS Country, " +
                "city.Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, continentName);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n All Capital Cities in " + continentName + " (Sorted by Population)");
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
                    System.out.println("  No capital cities found for continent: " + continentName);
                    System.out.println("  Please check the continent name and try again.");
                }
                
                System.out.println("==================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
