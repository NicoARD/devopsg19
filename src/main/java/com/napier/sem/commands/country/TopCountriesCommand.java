package com.napier.sem.commands.country;

import com.napier.sem.CommandBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to display top N countries by population.
 */
public class TopCountriesCommand extends CommandBase {
    
    public TopCountriesCommand() {
        super("top-countries", "Display top N countries by population (usage: top-countries <number>)");
    }
    
    /**
     * Retrieves and displays the top N most populated countries in the world.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        int count = 5; // default value
        
        // Parse the count argument if provided
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
        
        String query = "SELECT Name, Population, Continent FROM country ORDER BY Population DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, count);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Top " + count + " Countries by Population:");
                System.out.println("========================================");
                
                int rank = 1;
                while (rs.next()) {
                    String name = rs.getString("Name");
                    long population = rs.getLong("Population");
                    String continent = rs.getString("Continent");
                    System.out.printf("%d. %s (%s) - %,d people%n", rank++, name, continent, population);
                }
                
                if (rank == 1) {
                    System.out.println("No countries found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            throw e;
        }
    }
}