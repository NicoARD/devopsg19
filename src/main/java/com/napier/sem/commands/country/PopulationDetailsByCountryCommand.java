package com.napier.sem.commands.country;

import com.napier.sem.DatabaseConfig;
import com.napier.sem.ICommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class PopulationDetailsByCountryCommand implements ICommand {

    @Override
    public String getName() {
        return "population-details-country";
    }

    @Override
    public void execute(String[] args) {
        try {
            // -------------------------
            // INPUT VALIDATION
            // -------------------------
            if (args.length < 1) {
                System.out.println("Usage: population-details-country <country_name>");
                return;
            }

            String countryName = args[0];

            if (countryName.trim().isEmpty()) {
                System.out.println("Error: country name cannot be empty.");
                return;
            }

            // -------------------------
            // SQL QUERY
            // -------------------------
            String sql = "SELECT " +
                    "    co.Name AS Country, " +
                    "    co.Population AS TotalPopulation, " +
                    "    SUM(ci.Population) AS UrbanPopulation, " +
                    "    (co.Population - SUM(ci.Population)) AS NonUrbanPopulation " +
                    "FROM country co " +
                    "LEFT JOIN city ci ON ci.CountryCode = co.Code " +
                    "WHERE co.Name = ? " +
                    "GROUP BY co.Name, co.Population;";

            Connection conn = DatabaseConfig.getConnection();

            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, countryName);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No data found for country: " + countryName);
                return;
            }

            long total = rs.getLong("TotalPopulation");
            long urban = rs.getLong("UrbanPopulation");
            long nonUrban = rs.getLong("NonUrbanPopulation");

            System.out.println("\nPopulation Details for " + countryName);
            System.out.println("===========================================");
            System.out.printf("Total Population:        %,d%n", total);
            System.out.printf("Urban (City) Population: %,d%n", urban);
            System.out.printf("Non-Urban Population:    %,d%n", nonUrban);

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.out.println("Error executing population-details-country: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
