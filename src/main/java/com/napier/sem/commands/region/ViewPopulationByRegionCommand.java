package com.napier.sem.commands.region;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to view population details by region
 * Shows total, urban, and non-urban populations for a specific region
 */
public class ViewPopulationByRegionCommand extends CommandBase {

    public ViewPopulationByRegionCommand() {
        super("region-pop", "View population details for a region (usage: region-pop <region_name>)");
    }

    /**
     * Retrieves and displays population details for a specific region including total, urban, and non-urban populations.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // Validate input
        if (args.length < 2 || args[1].trim().isEmpty()) {
            System.out.println("Please provide a valid region name. Example: regionpop 'Eastern Asia'");
            return;
        }

        String region = args[1].trim();

        // SQL query to fetch population details for a region
        String query = "SELECT " +
                "r.Region AS Region, " +
                "SUM(c.Population) AS TotalPopulation, " +
                "SUM(ci.Population) AS UrbanPopulation, " +
                "(SUM(c.Population) - SUM(ci.Population)) AS NonUrbanPopulation " +
                "FROM country c " +
                "JOIN city ci ON c.Code = ci.CountryCode " +
                "JOIN ( " +
                "SELECT DISTINCT Region FROM country " +
                ") r ON c.Region = r.Region " +
                "WHERE c.Region LIKE ? " +
                "GROUP BY r.Region " +
                "ORDER BY TotalPopulation DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + region + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No data found for region: " + region);
                    return;
                }

                String headerFormat = "%-30s %-15s %-15s %-15s%n";
                
                System.out.println("Population Details by Region:");
                System.out.println(TableFormatter.generateSeparator(headerFormat));
                System.out.printf(headerFormat,
                        "Region", "Total", "Urban", "Non-Urban");
                System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

                while (rs.next()) {
                    String regionName = rs.getString("Region");
                    long total = rs.getLong("TotalPopulation");
                    long urban = rs.getLong("UrbanPopulation");
                    long nonUrban = rs.getLong("NonUrbanPopulation");

                    System.out.printf("%-30s %,15d %,15d %,15d%n",
                            regionName, total, urban, nonUrban);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing population by region query: " + e.getMessage());
            throw e;
        }
    }
}
