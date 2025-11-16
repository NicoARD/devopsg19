package com.napier.sem.commands;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TEAM-Jǔjù Feature Command
 * Feature: View Population of a Specific City
 *
 * User Story:
 *  As a Data Analyst, I want to view the population of a specific city
 *  so that I can get detailed urban data and compare urban vs national context.
 *
 * Usage: citypop <city_name>
 */
public class CityPopulationCommand extends CommandBase {

    public CityPopulationCommand() {
        super("citypop", "Display the population of a specific city (usage: citypop <city_name>)");
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {

        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a city name. Usage: citypop <city_name>");
            return;
        }

        String cityName = args[1].trim();

        if (cityName.isEmpty()) {
            System.out.println(" Invalid input. City name cannot be empty.");
            return;
        }

        // ---- SQL Query ----
        // Fetch:
        // - City population
        // - Urban population (% share of its country’s city population)
        // - Non-urban population of that country
        String sql = """
                SELECT
                    ci.Name AS CityName,
                    ci.Population AS CityPopulation,
                    co.Name AS CountryName,
                    co.Population AS CountryPopulation,
                    (SELECT SUM(c2.Population)
                     FROM city c2
                     WHERE c2.CountryCode = co.Code) AS TotalUrbanPopulation
                FROM city ci
                JOIN country co ON ci.CountryCode = co.Code
                WHERE ci.Name = ?
                LIMIT 1;
                """;

        // ----  Execute Query ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cityName);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("No city found with the name: " + cityName);
                    return;
                }

                String name = rs.getString("CityName");
                String country = rs.getString("CountryName");
                long cityPop = rs.getLong("CityPopulation");
                long countryPop = rs.getLong("CountryPopulation");
                long totalUrban = rs.getLong("TotalUrbanPopulation");

                if (rs.wasNull()) {
                    totalUrban = 0;
                }

                long nonUrban = countryPop - totalUrban;

                // ---- Display Results ----
                System.out.println("\n Population Report for City: " + name);
                System.out.println("Country: " + country);
                System.out.println("==============================================");
                System.out.printf("City Population:        %,d%n", cityPop);
                System.out.printf("Country Total Pop:      %,d%n", countryPop);
                System.out.printf("Urban Pop (Country):    %,d%n", totalUrban);
                System.out.printf("Non-Urban Pop:          %,d%n", nonUrban);
            }
        } catch (SQLException e) {
            System.out.println(" Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
