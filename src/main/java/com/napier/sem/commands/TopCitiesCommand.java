package com.napier.sem.commands;

import com.napier.sem.CommandBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to display top N cities by population
 */
public class TopCitiesCommand extends CommandBase {

    public TopCitiesCommand() {
        super("topcities", "Display top N cities by population (usage: topcities <number>)");
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        int count = 5; // Default value

        // Input validation for N
        if (args.length > 1) {
            try {
                count = Integer.parseInt(args[1]);
                if (count <= 0) {
                    System.out.println("Count must be a positive number. Using default value of 5.");
                    count = 5;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Using default value of 5.");
                count = 5;
            }
        }

        String query = "SELECT c.Name, c.Population, c.District, co.Name AS Country "
                + "FROM city c "
                + "JOIN country co ON c.CountryCode = co.Code "
                + "ORDER BY c.Population DESC "
                + "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, count);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Top " + count + " Cities by Population:");
                System.out.println("========================================");

                int rank = 1;
                while (rs.next()) {
                    String cityName = rs.getString("Name");
                    String country = rs.getString("Country");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%d. %s, %s (%s) - %,d people%n",
                            rank++, cityName, district, country, population);
                }

                if (rank == 1) {
                    System.out.println("No cities found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            throw e;
        }
    }
}
