package com.napier.sem.commands;

import com.napier.sem.CommandBase;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

/**



 * Feature: View Population of a Specific Country

 * User Story:

 *  As a Data Analyst, I want to view the population of a specific country

 *  so that I can examine national demographics.

 *

 * Usage: countrypop <country_name>

 */

public class CountryPopulationCommand extends CommandBase {

    public CountryPopulationCommand() {
        super("countrypop", "Display the population of a specific country (usage: countrypop <country_name>)");
    }

    @Override

    public void execute(Connection connection, String[] args) throws SQLException {

        // ----  Input Validation ----

        if (args.length < 2) {

            System.out.println("  Please provide a country name. Usage: countrypop <country_name>");

            return;

        }

        String countryName = args[1].trim();

        if (countryName.isEmpty()) {

            System.out.println(" Invalid input. Country name cannot be empty.");

            return;

        }

        // ----  SQL Query ----

        // We fetch:

        // - total population

        // - population living in cities (urban)

        // - population NOT living in cities (non-urban)

        String sql = """

                SELECT

                    c.Name AS Country,

                    c.Population AS TotalPopulation,

                    SUM(ci.Population) AS UrbanPopulation,

                    (c.Population - SUM(ci.Population)) AS NonUrbanPopulation

                FROM country c

                LEFT JOIN city ci ON c.Code = ci.CountryCode

                WHERE c.Name = ?

                GROUP BY c.Code;

                """;

        // ----  Execute Query with Error Handling ----

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, countryName);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {

                    System.out.println(" No country found with the name: " + countryName);

                    return;

                }

                String name = rs.getString("Country");

                long totalPop = rs.getLong("TotalPopulation");

                long urbanPop = rs.getLong("UrbanPopulation");

                long nonUrbanPop = rs.getLong("NonUrbanPopulation");

                // Handle null results if the country has no cities

                if (rs.wasNull()) {

                    urbanPop = 0;

                    nonUrbanPop = totalPop;

                }

                System.out.println("\n Population Report for " + name);

                System.out.println("==============================================");

                System.out.printf("Total Population:       %,d%n", totalPop);

                System.out.printf("Urban Population:       %,d%n", urbanPop);

                System.out.printf("Non-Urban Population:   %,d%n", nonUrbanPop);

            }

        } catch (SQLException e) {

            System.out.println(" Database query failed: " + e.getMessage());

            throw e;

        }

    }

}

