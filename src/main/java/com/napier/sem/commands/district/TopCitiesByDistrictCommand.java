package com.napier.sem.commands.district;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to display top N populated cities in a specific district.
 * Usage: topcities-district <district> <number>
 */
public class TopCitiesByDistrictCommand extends CommandBase {

    public TopCitiesByDistrictCommand() {
        super("topcities-district", "Display top N cities by population in a specific district (usage: topcities-district <district> <number>)");
    }

    /**
     * Retrieves and displays the top N most populated cities in a specific district.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- Input Validation ----
        if (args.length < 2) {
            System.out.println("Please provide a district name. Usage: topcities-district <district> <number>");
            return;
        }

        String district = args[1].trim();
        if (district.isEmpty()) {
            System.out.println("Invalid district name. It cannot be empty.");
            return;
        }

        int limit = 5; // default value
        if (args.length > 2) {
            try {
                limit = Integer.parseInt(args[2]);
                if (limit <= 0) {
                    System.out.println("Limit must be a positive number. Defaulting to 5.");
                    limit = 5;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for limit. Defaulting to 5.");
                limit = 5;
            }
        }

        // ---- SQL Query ----
        String sql = "SELECT c.Name, c.Population, co.Name AS Country " +
                "FROM city c " +
                "JOIN country co ON c.CountryCode = co.Code " +
                "WHERE c.District = ? " +
                "ORDER BY c.Population DESC " +
                "LIMIT ?";

        // ---- Execute Query ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, district);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Top " + limit + " Cities in District: " + district);
                System.out.println("================================================");

                int rank = 1;
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String name = rs.getString("Name");
                    String country = rs.getString("Country");
                    long population = rs.getLong("Population");

                    System.out.printf("%d. %s, %s — %,d people%n", rank++, country, name, population);
                }

                if (!found) {
                    System.out.println("No cities found for district: " + district);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
