package com.napier.sem.commands.global;

import com.napier.sem.CommandBase;
import com.napier.sem.utils.TableFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command to view global language distributions:
 * Shows population speaking Chinese, English, Hindi, Spanish, and Arabic,
 * sorted descending by population and percentage of world population.
 */
public class ViewGlobalLanguageDistributionCommand extends CommandBase {

    public ViewGlobalLanguageDistributionCommand() {
        super("language-dist", "Display number and percentage of people speaking Chinese, English, Hindi, Spanish, or Arabic globally.");
    }

    /**
     * Retrieves and displays global language distribution showing speakers and percentages for major world languages.
     */
    @Override
    public void execute(Connection connection, String[] args) throws SQLException {
        String headerFormat = "%-12s %-20s %-20s%n";
        
        System.out.println("\n Global Language Distribution Report");
        System.out.println(TableFormatter.generateSeparator(headerFormat));

        //Validate connection
        if (connection == null || connection.isClosed()) {
            System.out.println("Database connection is not available.");
            return;
        }

        //Query to fetch total world population
        String totalQuery = "SELECT SUM(Population) AS WorldPopulation FROM country";
        long worldPopulation = 0;

        try (PreparedStatement stmt = connection.prepareStatement(totalQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                worldPopulation = rs.getLong("WorldPopulation");
            }
        }

        if (worldPopulation == 0) {
            System.out.println("Could not retrieve world population data.");
            return;
        }

        //Query to fetch population per target language
        String languageQuery = "SELECT cl.Language, SUM(c.Population * (cl.Percentage / 100)) AS Speakers "
                + "FROM countrylanguage cl "
                + "JOIN country c ON cl.CountryCode = c.Code "
                + "WHERE cl.Language IN ('Chinese', 'English', 'Hindi', 'Spanish', 'Arabic') "
                + "GROUP BY cl.Language "
                + "ORDER BY Speakers DESC";

        try (PreparedStatement stmt = connection.prepareStatement(languageQuery);
             ResultSet rs = stmt.executeQuery()) {

            System.out.printf(headerFormat, "Language", "Speakers", "% of World Population");
            System.out.println(TableFormatter.generateDashedSeparator(headerFormat));

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String language = rs.getString("Language");
                long speakers = rs.getLong("Speakers");
                double percentage = (speakers / (double) worldPopulation) * 100.0;

                System.out.printf("%-12s %,20d %,20.2f%%%n",
                        language, speakers, percentage);
            }

            if (!hasData) {
                System.out.println("No language data found for the specified languages.");
            }
        } catch (SQLException e) {
            System.out.println("Error executing language query: " + e.getMessage());
        }

        System.out.println(TableFormatter.generateSeparator(headerFormat));
        System.out.println("Report complete.\n");
    }
}
