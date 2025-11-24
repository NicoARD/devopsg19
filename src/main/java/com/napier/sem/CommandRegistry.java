package com.napier.sem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.napier.sem.commands.city.*;
import com.napier.sem.commands.continent.*;
import com.napier.sem.commands.country.*;
import com.napier.sem.commands.district.*;
import com.napier.sem.commands.region.*;
import com.napier.sem.commands.global.*;

/**
 * Registry for managing commands
 */
public class CommandRegistry {
    
    private static final Map<String, ICommand> commands = new HashMap<>();
    
    /**
     * Initialize and register all available commands
     */
    public static void initializeCommands() {
        // Register commands here - using their execution command from the object
        registerCommand(new TopCountriesCommand());
        registerCommand(new TopCitiesCommand());
        registerCommand(new ViewPopulationByRegionCommand());
        registerCommand(new ViewGlobalLanguageDistributionCommand());
        registerCommand(new ViewPopulationByDistrictCommand());
        registerCommand(new TopCapitalCitiesCommand());
        registerCommand(new CountryPopulationCommand());
        registerCommand(new CityPopulationCommand());
        registerCommand(new AllCitiesCountryCommand());
        registerCommand(new TopCitiesByDistrictCommand());
        registerCommand(new TopCapitalCitiesByRegionCommand());
        registerCommand(new TopCapitalCitiesByContinentCommand());
        registerCommand(new PopulationDetailsByCountryCommand());
        registerCommand(new CapitalCitiesByRegionCommand());

    }

    /**
     * Register a command using its execution command
     * @param command Command implementation
     */
    public static void registerCommand(ICommand command) {
        commands.put(command.getExcecutionCommand().toLowerCase(), command);
    }
    
    /**
     * Register a command with a specific name (for backwards compatibility or aliasing)
     * @param name Command name
     * @param command Command implementation
     * @deprecated Use {@link #registerCommand(ICommand)} instead to use the command's execution command
     */
    @Deprecated
    public static void registerCommand(String name, ICommand command) {
        commands.put(name.toLowerCase(), command);
    }
    
    /**
     * Get a command by name
     * @param name Command name
     * @return Command implementation or null if not found
     */
    public static ICommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    /**
     * Check if a command is registered
     * @param name Command name
     * @return true if command exists
     */
    public static boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }
    
    /**
     * Get all registered commands
     * @return Map of command names to implementations
     */
    public static Map<String, ICommand> getAllCommands() {
        return new HashMap<>(commands);
    }

}