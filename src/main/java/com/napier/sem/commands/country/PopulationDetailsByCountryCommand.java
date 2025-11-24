package com.napier.sem.commands.country;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * View Population Details of a Specific Country
 */
public class PopulationDetailsByCountryCommand extends CommandBase {

    public PopulationDetailsByCountryCommand() {
        super("populationdetailscountry", "Display population details for a specific country (usage: population-details-country <country_name>)");
    }

    /**
     * Retrieves and displays population details for a specific country including urban and non-urban breakdown.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ----  Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a country name. Usage: population-details-country <country_name>");
            return;
        }

        String countryName = args[1].trim();

        if (countryName.isEmpty()) {
            System.out.println(" Invalid input. Country name cannot be empty.");
            return;
        }

        String sql = "SELECT " +
                "c.Name AS Country, " +
                "c.Population AS TotalPopulation, " +
                "SUM(ci.Population) AS UrbanPopulation, " +
                "(c.Population - SUM(ci.Population)) AS NonUrbanPopulation " +
                "FROM country c " +
                "LEFT JOIN city ci ON c.Code = ci.CountryCode " +
                "WHERE c.Name = ? " +
                "GROUP BY c.Code";

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

                System.out.println("\n Population Details for " + name);
                System.out.println("===========================================");
                System.out.printf("Total Population:        %,d%n", totalPop);
                System.out.printf("Urban (City) Population: %,d%n", urbanPop);
                System.out.printf("Non-Urban Population:    %,d%n", nonUrbanPop);
            }
        } catch (SQLException e) {
            System.out.println(" Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
