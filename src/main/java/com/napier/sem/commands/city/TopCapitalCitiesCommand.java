package com.napier.sem.commands.city;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TEAM-Jǔjù Feature Command
 * Feature: Top N Populated Capital Cities (World)
 * User Story: As a Data Analyst, I want to view the top N populated capital cities in the world
 * so that I can identify the most influential capitals.
 *
 * Usage: topcapitals <number>
 */
public class TopCapitalCitiesCommand extends CommandBase {

    public TopCapitalCitiesCommand() {
        super("topcapitals", "Display the top N populated capital cities in the world (usage: topcapitals <number>)");
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        int limit = 10; // Default value

        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit <= 0) {
                    System.out.println("Limit must be a positive number. Defaulting to 10.");
                    limit = 10;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for limit. Defaulting to 10.");
                limit = 10;
            }
        }

        // ---- SQL Query ----
        String sql = """
                SELECT city.Name AS Capital, country.Name AS Country, city.Population
                FROM city
                JOIN country ON city.ID = country.Capital
                WHERE city.Population IS NOT NULL
                ORDER BY city.Population DESC
                LIMIT ?;
                """;

        // ---- Execute Query ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nTop " + limit + " Most Populated Capital Cities in the World");
                System.out.println("===============================================================");
                System.out.printf("%-5s %-30s %-30s %-15s%n", "No.", "Capital City", "Country", "Population");
                System.out.println("--------------------------------------------------------------------------");

                int rank = 1;
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    String capital = rs.getString("Capital");
                    String country = rs.getString("Country");
                    long population = rs.getLong("Population");

                    System.out.printf("%-5d %-30s %-30s %,d%n", rank++, capital, country, population);
                }

                if (!found) {
                    System.out.println("No capital cities found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
