package com.napier.sem.commands;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to display total population of a specific district
 */
public class ViewPopulationByDistrictCommand extends CommandBase {

    public ViewPopulationByDistrictCommand() {
        super("districtpop", "View population of a specific district (usage: districtpop <district_name>)");
    }

    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // Validate input
        if (args.length < 2 || args[1].trim().isEmpty()) {
            System.out.println("ERROR: Please provide a valid district name. Usage: districtpop <district_name>");
            return;
        }

        String districtName = args[1].trim();

        // SQL query to get population for a district
        String query = "SELECT SUM(Population) AS TotalPopulation "
                + "FROM city "
                + "WHERE District = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, districtName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long totalPop = rs.getLong("TotalPopulation");

                    if (rs.wasNull() || totalPop == 0) {
                        System.out.println("WARNING: No data found for district: " + districtName);
                    } else {
                        System.out.printf("Total population of district '%s' is %,d people.%n",
                                districtName, totalPop);
                    }
                } else {
                    System.out.println("WARNING: No data found for district: " + districtName);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error executing query: " + e.getMessage());
            throw e;
        }
    }
}
