package com.napier.sem.commands.country;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TEAM-Jǔjù Feature Command
 * Feature: View Top N Cities of a Specific Country
 *
 * User Story:
 *  As a Data Analyst, I want to view the top N populated cities in a specific country
 *  so that I can compare major cities within a country.
 *
 * Usage: topcities-country <country_name> <N>
 */
public class TopNCitiesByCountryCommand extends CommandBase {

    public TopNCitiesByCountryCommand() {
        super(
                "top-cities-country",
                "Displays the top N cities in a specific country ordered by population (usage: top-cities-country <country_name> <N>)"
        );
    }

    /**
     * Retrieves and displays the top N cities for a specific country including
     * city name and population.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {

        // ---- Input Validation ----
        if (args.length < 3) {
            System.out.println("  Please provide a country name and N. Usage: topcities-country <country_name> <N>");
            return;
        }

        String countryName = args[1].trim();
        if (countryName.isEmpty()) {
            System.out.println(" Invalid input. Country name cannot be empty.");
            return;
        }

        int n;
        try {
            n = Integer.parseInt(args[2]);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input. N must be a positive integer.");
            return;
        }

        if (connection == null) {
            System.out.println(" Database connection unavailable.");
            return;
        }

        // ---- SQL Query ----
            String sql = """
                    SELECT city.Name, city.Population
                    FROM city
                    JOIN country ON city.CountryCode = country.Code
                    WHERE country.Name = ?
                    ORDER BY city.Population DESC
                    LIMIT ?
                    """;        // ---- Execute Query ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, countryName);
            stmt.setInt(2, n);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No cities found for country: " + countryName);
                    return;
                }

                // Print header
                String headerFormat = "%-30s %15s%n";
                System.out.println("\n" + TableFormatter.generateSeparator(headerFormat));
                System.out.printf("Top %d Cities in Country: %s%n", n, countryName);
                System.out.println(TableFormatter.generateSeparator(headerFormat));
                System.out.printf(headerFormat, "City", "Population");
                System.out.println(TableFormatter.generateDashedSeparator(headerFormat));
                
                while (rs.next()) {
                    String cityName = rs.getString("Name");
                    int population = rs.getInt("Population");
                    System.out.printf(headerFormat, cityName, String.format("%,d", population));
                }
            }

        } catch (SQLException e) {
            System.out.println(" Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
