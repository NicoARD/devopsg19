package com.napier.sem.commands.world;

import com.napier.sem.CommandBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve the total population of the world.
 * User Story: As a Data Analyst, I want to view the total population of the world 
 * so that I can get a global overview.
 */
public class TotalWorldPopulationCommand extends CommandBase {

    public TotalWorldPopulationCommand() {
        super("population-world", "Display the total population of the world (usage: population-world)");
    }

    /**
     * Retrieves and displays the total world population by summing all country populations.
     * 
     * @param connection Database connection
     * @param args Command arguments (none required)
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- SQL Query to Calculate Total World Population ----
        String sql = "SELECT SUM(Population) AS TotalPopulation FROM country";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long totalPopulation = rs.getLong("TotalPopulation");
                    
                    System.out.println("\n Total World Population");
                    System.out.println("========================================");
                    System.out.printf("  %,d%n", totalPopulation);
                    System.out.println("========================================\n");
                } else {
                    System.out.println("  No population data found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
