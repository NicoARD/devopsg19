package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TEAM-Jǔjù Feature Command
 * Feature: View Population of a Specific Region
 *
 * User Story:
 *  As a Data Analyst, I want to view the population of a specific region
 *  so that I can compare total, urban, and rural population distribution.
 *
 * Usage: population-region <region_name>
 */
public class PopulationByRegionCommand extends CommandBase {

    public PopulationByRegionCommand() {
        super(
                "population-region",
                "Displays total, urban, and non-urban population for a specific region (usage: population-region <region_name>)"
        );
    }

    /**
     * Retrieves and displays population details for a specific region including
     * total population, urban population, and non-urban population.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {

        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("  Please provide a region name. Usage: population-region <region_name>");
            return;
        }

        String regionName = args[1].trim();

        if (regionName.isEmpty()) {
            System.out.println(" Invalid input. Region name cannot be empty.");
            return;
        }

        // ---- SQL Query ----
        String sql = """
                SELECT
                    co.Region AS Region,
                    SUM(co.Population) AS TotalPopulation,
                    SUM(ci.Population) AS UrbanPopulation,
                    (SUM(co.Population) - SUM(ci.Population)) AS NonUrbanPopulation
                FROM country co
                LEFT JOIN city ci ON ci.CountryCode = co.Code
                WHERE co.Region = ?
                GROUP BY co.Region;
                """;

        if (connection == null) {
            System.out.println(" Database connection unavailable.");
            return;
        }

        // ---- Execute Query ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionName);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("No population data found for region: " + regionName);
                    return;
                }

                long total = rs.getLong("TotalPopulation");
                long urban = rs.getLong("UrbanPopulation");

                if (rs.wasNull()) {
                    urban = 0;
                }

                long nonUrban = total - urban;

                // ---- Display Results ----
                System.out.println("\n Population Report for Region: " + regionName);
                System.out.println("==============================================");
                System.out.printf("Total Population:        %,d%n", total);
                System.out.printf("Urban Population:        %,d%n", urban);
                System.out.printf("Non-Urban Population:    %,d%n", nonUrban);
            }

        } catch (SQLException e) {
            System.out.println(" Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
