package com.napier.sem.commands.city;

import com.napier.sem.CommandBase;
import java.sql.*;

/**
 * Command to display all cities in a specific country ordered by population (largest to smallest).
 */
public class AllCitiesCountryCommand extends CommandBase {

    public AllCitiesCountryCommand() {
        super("cities-country", "Display all cities in a country ordered by population (usage: cities-country <country_name>)");
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // Validate input
        if (args.length < 2 || args[1].trim().isEmpty()) {
            System.out.println("ERROR: Please provide a valid country name. Usage: cities-country <country_name>");
            return;
        }

        String countryName = args[1].trim();

        // SQL query: join city and country, order by population
        String query = "SELECT c.Name AS CityName, c.District, c.Population, co.Name AS Country "
                + "FROM city c "
                + "JOIN country co ON c.CountryCode = co.Code "
                + "WHERE co.Name LIKE ? "
                + "ORDER BY c.Population DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Partial matching support
            stmt.setString(1, "%" + countryName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("All Cities in " + countryName + " (ordered by population):");
                System.out.println("==============================================================");

                int count = 0;
                while (rs.next()) {
                    String cityName = rs.getString("CityName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");
                    String country = rs.getString("Country");

                    System.out.printf("%d. %s (%s, %s) - %,d people%n",
                            ++count, cityName, district, country, population);
                }

                if (count == 0) {
                    System.out.println("WARNING: No cities found for country: " + countryName);
                } else {
                    System.out.println("==============================================================");
                    System.out.println(count + " cities found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error executing query: " + e.getMessage());
            throw e;
        }
    }
}
