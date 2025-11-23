package com.napier.sem.commands.region;

import com.napier.sem.DatabaseConfig;
import com.napier.sem.ICommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopCapitalCitiesByRegionCommand implements ICommand {

    public String getName() {
        return "top-capital-cities-region";
    }

    public void execute(String[] args) {
        try {
            // -------------------------
            // INPUT VALIDATION
            // -------------------------
            if (args.length < 2) {
                System.out.println("Usage: top-capital-cities-region <region> <N>");
                return;
            }

            String region = args[0];
            int limit;

            try {
                limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Error: N must be a valid integer.");
                return;
            }

            if (limit <= 0) {
                System.out.println("Error: N must be greater than zero.");
                return;
            }

            // -------------------------
            // SQL QUERY
            // -------------------------
            String sql =
                    "SELECT city.Name AS CapitalCity, city.Population, country.Region " +
                            "FROM city " +
                            "JOIN country ON city.ID = country.Capital " +
                            "WHERE country.Region = ? " +
                            "ORDER BY city.Population DESC " +
                            "LIMIT ?;";

            Connection conn = DatabaseConfig.getConnection();

            // Use try-with-resources to ensure proper cleanup
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, region);
                stmt.setInt(2, limit);

                try (ResultSet rs = stmt.executeQuery()) {

                    System.out.printf("%-35s %-15s %-20s%n",
                            "Capital City", "Population", "Region");
                    System.out.println("------------------------------------------------------------------");

                    boolean dataFound = false;

                    while (rs.next()) {
                        dataFound = true;
                        System.out.printf("%-35s %-15d %-20s%n",
                                rs.getString("CapitalCity"),
                                rs.getInt("Population"),
                                rs.getString("Region"));
                    }

                    if (!dataFound) {
                        System.out.println("No results found for region: " + region);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error running command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getExcecutionCommand() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {

    }
}
