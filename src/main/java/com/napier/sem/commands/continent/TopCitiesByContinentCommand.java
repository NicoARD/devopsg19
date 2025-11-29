package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve the top N populated cities in a continent.
 * User Story: As a Data Analyst, I want to view the top N populated cities in a specific continent 
 * so that I can prioritize analysis in that area.
 */
public class TopCitiesByContinentCommand extends CommandBase {

    public TopCitiesByContinentCommand() {
        super("top-cities-continent", "Display top N cities in a continent by population (usage: top-cities-continent <continent> <N>)");
    }

    /**
     * Retrieves and displays the top N most populated cities in a specific continent sorted by population.
     * 
     * @param connection Database connection
     * @param args Command arguments where args[1] is the continent name and args[2] is the limit
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // -------------------------
        // INPUT VALIDATION
        // -------------------------
        if (args.length < 3) {
            System.out.println("  Usage: top-cities-continent <continent> <N>");
            return;
        }

        String continent = args[1];
        int limit;

        try {
            limit = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("  Error: N must be a valid integer.");
            return;
        }

        if (limit <= 0) {
            System.out.println("  Error: N must be greater than zero.");
            return;
        }

        // -------------------------
        // SQL QUERY
        // -------------------------
        String sql = "SELECT " +
                "city.Name AS CityName, " +
                "country.Name AS Country, " +
                "city.District, " +
                "city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC " +
                "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, continent);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;

                System.out.println("\n Top " + limit + " Cities in " + continent + " by Population");
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
                    System.out.println("  No results found for continent: " + continent);
                }

                System.out.println("==================================================================\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
