package com.napier.sem.commands.city;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all capital cities in the world sorted by population.
 * User Story: As a Data Analyst, I want to view all capital cities in the world 
 * sorted by largest population to smallest so that I can compare global capitals.
 */
public class AllCapitalCitiesCommand extends CommandBase {

    public AllCapitalCitiesCommand() {
        super("all-capitals", "Display all capital cities in the world sorted by population (usage: all-capitals)");
    }

    /**
     * Retrieves and displays all capital cities in the world sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments (no additional arguments required)
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- SQL Query to retrieve all capital cities ----
        String sql = "SELECT " +
                "city.Name AS CapitalCity, " +
                "country.Name AS Country, " +
                "city.Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Capital IS NOT NULL " +
                "ORDER BY city.Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            boolean dataFound = false;
            
            String headerFormat = "%-35s %-30s %15s%n";
            
            System.out.println("\nAll Capital Cities in the World (Sorted by Population)");
            System.out.println(TableFormatter.generateSeparator(headerFormat));
            System.out.printf(headerFormat, "Capital City", "Country", "Population");
            System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

            while (rs.next()) {
                dataFound = true;
                String capitalCity = rs.getString("CapitalCity");
                String country = rs.getString("Country");
                long population = rs.getLong("Population");

                System.out.printf("%-35s %-30s %,15d%n", capitalCity, country, population);
            }

            if (!dataFound) {
                System.out.println("  No capital cities found in the database.");
            }
            
            System.out.println(TableFormatter.generateSeparator(headerFormat) + "\n");
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
