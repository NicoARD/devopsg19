package com.napier.sem.commands.country;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve all countries in the world sorted by population.
 * User Story: As a Data Analyst, I want to view all countries in the world 
 * sorted by largest population to smallest so that I can analyze global population distribution.
 */
public class AllCountriesCommand extends CommandBase {

    public AllCountriesCommand() {
        super("all-countries", "Display all countries in the world sorted by population (usage: all-countries)");
    }

    /**
     * Retrieves and displays all countries in the world sorted by population (largest to smallest).
     * 
     * @param connection Database connection
     * @param args Command arguments (no additional arguments required)
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- SQL Query ----
        String sql = "SELECT " +
                "Code, " +
                "Name, " +
                "Continent, " +
                "Region, " +
                "Population, " +
                "Capital " +
                "FROM country " +
                "ORDER BY Population DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                System.out.println("\n All Countries in the World (Sorted by Population)");
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
                    System.out.println("  No countries found in the database.");
                }
                
                System.out.println("=======================================================================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
