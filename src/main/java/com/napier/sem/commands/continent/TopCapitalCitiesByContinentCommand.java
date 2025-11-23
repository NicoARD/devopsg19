package com.napier.sem.commands.continent;

import com.napier.sem.DatabaseConfig;
import com.napier.sem.ICommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopCapitalCitiesByContinentCommand implements ICommand {

    public String getName() {
        return "top-capital-cities-continent";
    }

    public void execute(String[] args) {

        try {
            // -------------------------
            // INPUT VALIDATION
            // -------------------------
            if (args.length < 2) {
                System.out.println("Usage: top-capital-cities-continent <continent> <N>");
                return;
            }

            String continent = args[0];
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
                    "SELECT city.Name AS CapitalCity, city.Population, country.Continent " +
                            "FROM city " +
                            "JOIN country ON city.ID = country.Capital " +
                            "WHERE country.Continent = ? " +
                            "ORDER BY city.Population DESC " +
                            "LIMIT ?;";

            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, continent);
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();

            System.out.printf("%-35s %-15s %-20s%n", "Capital City", "Population", "Continent");
            System.out.println("------------------------------------------------------------------");

            boolean dataFound = false;

            while (rs.next()) {
                dataFound = true;
                System.out.printf(
                        "%-35s %-15d %-20s%n",
                        rs.getString("CapitalCity"),
                        rs.getInt("Population"),
                        rs.getString("Continent")
                );
            }

            if (!dataFound) {
                System.out.println("No results found for continent: " + continent);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
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
