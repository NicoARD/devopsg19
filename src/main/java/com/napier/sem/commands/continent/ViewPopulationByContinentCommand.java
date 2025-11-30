package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to view total, urban, and rural population of a specific continent.
 */
public class ViewPopulationByContinentCommand extends CommandBase {

    /**
     * Constructor defining command name and description for CLI help.
     */
    public ViewPopulationByContinentCommand() {
        super(
                "continent-pop",
                "Display total, urban, and rural population of a continent (usage: continent-pop <continent>)"
        );
    }

    /**
     * Executes the command.
     *
     * @param connection Database connection
     * @param args Command arguments where args[1] is the continent name
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // -------------------------
        // INPUT VALIDATION
        // -------------------------
        if (args.length < 2 || args[1].trim().isEmpty()) {
            System.out.println("  Usage: continent-pop <continent>");
            return;
        }

        String continent = args[1].trim();

        // -------------------------
        // SQL QUERY
        // -------------------------
        String sql = """
            SELECT SUM(country.Population) AS TotalPopulation,
                   SUM(city.Population) AS UrbanPopulation,
                   SUM(country.Population) - SUM(city.Population) AS RuralPopulation
            FROM country
            LEFT JOIN city ON country.Code = city.CountryCode
            WHERE country.Continent = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long total = rs.getLong("TotalPopulation");
                    long urban = rs.getLong("UrbanPopulation");
                    long rural = rs.getLong("RuralPopulation");

                    String format = "%49s%n";
                    
                    System.out.println("\nPopulation statistics for continent: " + continent);
                    System.out.println(TableFormatter.generateSeparator(format));
                    System.out.printf("Total population : %,d%n", total);
                    System.out.printf("Urban population : %,d%n", urban);
                    System.out.printf("Rural population : %,d%n", rural);
                    System.out.println(TableFormatter.generateSeparator(format) + "\n");
                } else {
                    System.out.println("  No results found for continent: " + continent);
                }
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
