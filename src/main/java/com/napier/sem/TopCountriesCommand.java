package com.napier.sem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to get top N countries by population
 */
public class TopCountriesCommand implements ICommand {
    
    @Override
    public String getDescription() {
        return "Get top N countries by population";
    }
    
    @Override
    public void execute(Connection connection, String[] args) {
        // Default count is 5
        int count = 5;
        
        // Parse the count argument if provided
        if (args.length > 1) {
            try {
                count = Integer.parseInt(args[1]);
                if (count <= 0) {
                    System.out.println("Error: Count must be a positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format. Using default count of 5");
                count = 5;
            }
        }
        
        String sampleQuery = "SELECT Name, Population, Continent FROM country ORDER BY Population DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sampleQuery)) {
            stmt.setInt(1, count);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Top " + count + " Countries by Population:");
                
                while (rs.next()) {
                    String name = rs.getString("Name");
                    long population = rs.getLong("Population");
                    String continent = rs.getString("Continent");
                    System.out.printf("  %s (%s) - %,d people%n", name, continent, population);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }
}