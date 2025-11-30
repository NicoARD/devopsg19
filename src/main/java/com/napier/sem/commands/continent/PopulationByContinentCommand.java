package com.napier.sem.commands.continent;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to retrieve population details by continent.
 * User Story: As a Data Analyst, I want to view the population of people, people living in cities, 
 * and people not living in cities for each continent so that I can understand urbanization trends per continent.
 */
public class PopulationByContinentCommand extends CommandBase {

    public PopulationByContinentCommand() {
        super("population-continent", "Display population details for each continent (usage: population-continent)");
    }

    /**
     * Retrieves and displays population statistics for each continent, including:
     * - Total population
     * - Population living in cities (urban)
     * - Population not living in cities (rural)
     * - Percentage of urban vs rural population
     * 
     * @param connection Database connection
     * @param args Command arguments (none required)
     * @throws SQLException if database operation fails
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        // ---- SQL Query to Calculate Population Statistics by Continent ----
        String sql = "SELECT " +
                "co.Continent, " +
                "SUM(co.Population) AS TotalPopulation, " +
                "SUM(ci.Population) AS UrbanPopulation " +
                "FROM country co " +
                "LEFT JOIN city ci ON ci.CountryCode = co.Code " +
                "GROUP BY co.Continent " +
                "ORDER BY TotalPopulation DESC";

        // ---- Execute Query with Error Handling ----
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                boolean dataFound = false;
                
                String headerFormat = "%-20s %15s %15s %15s %12s %12s%n";
                
                System.out.println("\n Population Details by Continent");
                System.out.println(TableFormatter.generateSeparator(headerFormat));
                System.out.printf(headerFormat, 
                    "Continent", "Total Pop.", "Urban Pop.", "Rural Pop.", "Urban %", "Rural %");
                System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

                while (rs.next()) {
                    dataFound = true;
                    String continent = rs.getString("Continent");
                    long totalPopulation = rs.getLong("TotalPopulation");
                    long urbanPopulation = rs.getLong("UrbanPopulation");
                    
                    // Handle null urban population (countries with no cities in database)
                    if (rs.wasNull()) {
                        urbanPopulation = 0;
                    }
                    
                    long ruralPopulation = totalPopulation - urbanPopulation;
                    
                    // Calculate percentages
                    double urbanPercent = totalPopulation > 0 ? (urbanPopulation * 100.0 / totalPopulation) : 0.0;
                    double ruralPercent = totalPopulation > 0 ? (ruralPopulation * 100.0 / totalPopulation) : 0.0;

                    System.out.printf("%-20s %,15d %,15d %,15d %11.2f%% %11.2f%%%n", 
                        continent, totalPopulation, urbanPopulation, ruralPopulation, 
                        urbanPercent, ruralPercent);
                }

                if (!dataFound) {
                    System.out.println("  No population data found.");
                }
                
                System.out.println(TableFormatter.generateSeparator(headerFormat) + "\n");
            }
        } catch (SQLException e) {
            System.out.println("  Database query failed: " + e.getMessage());
            throw e;
        }
    }
}
