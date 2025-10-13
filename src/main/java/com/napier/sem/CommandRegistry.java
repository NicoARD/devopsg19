package com.napier.sem;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for managing commands
 */
public class CommandRegistry {
    
    private static final Map<String, ICommand> commands = new HashMap<>();
    
    /**
     * Register a command with a name
     * @param name Command name
     * @param command Command implementation
     */
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
    
    /**
     * Initialize and register all available commands
     */
    public static void initializeCommands() {
        registerCommand("topcountries", new TopCountriesCommand());
        // Add more commands here as they are created
    }
}