package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve the top N populated capital cities in a continent.
 */
public class TopCapitalCitiesByContinentCommand extends CommandBase {

    public TopCapitalCitiesByContinentCommand() {
        super("top-capital-cities-continent", "Display top N capital cities in a continent by population (usage: top-capital-cities-continent <continent> <N>)");
    }

    /**
     * Retrieves and displays the top N capital cities in a specific continent sorted by population.
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
            System.out.println("  Usage: top-capital-cities-continent <continent> <N>");
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
        String sql =
                "SELECT city.Name AS CapitalCity, country.Name AS Country, city.Population, country.Continent " +
                        "FROM city " +
                        "JOIN country ON city.ID = country.Capital " +
                        "WHERE country.Continent = ? " +
                        "ORDER BY city.Population DESC " +
                        "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, continent);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;

                String headerFormat = "%-35s %-30s %15s%n";

                System.out.println("\n Top " + limit + " Capital Cities in " + continent + " by Population");
                System.out.println(TableFormatter.generateSeparator(headerFormat));
                System.out.printf(headerFormat, "Capital City", "Country", "Population");
                System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

                while (rs.next()) {
                    dataFound = true;
                    System.out.printf(
                            "%-35s %-30s %,15d%n",
                            rs.getString("CapitalCity"),
                            rs.getString("Country"),
                            rs.getInt("Population")
                    );
                }

                if (!dataFound) {
                    System.out.println("  No results found for continent: " + continent);
                }

                System.out.println(TableFormatter.generateSeparator(headerFormat) + "\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
