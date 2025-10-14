package com.napier.sem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class for SEM Methods project
 * Includes database connectivity testing and command interface
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("SEM Methods Application Starting...");
        System.out.println("=======================================");
        
        // Initialize command registry
        CommandRegistry.initializeCommands();
        
        System.out.println("Starting command interface...");
        runCommandInterface();

        // Gracefully close database connections
        DatabaseConfig.closeDataSource();
        System.out.println("Application completed successfully!");
    }
    
    /**
     * Run the command interface
     */
    private static void runCommandInterface() {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("Database connection established for command interface");
            System.out.println("Type 'exit' to quit");
            System.out.println();
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Goodbye!");
                    break;
                }
                
                String[] commandArgs = input.split("\\s+");
                String commandName = commandArgs[0].toLowerCase();
                
                if (CommandRegistry.hasCommand(commandName)) {
                    ICommand command = CommandRegistry.getCommand(commandName);
                    try {
                        command.execute(connection, commandArgs);
                    } catch (Exception e) {
                        System.out.println("Error executing command: " + e.getMessage());
                    }
                } else {
                    if ("help".equals(commandName) || "?".equals(commandName)) {
                        printAvailableCommands();
                    } else {
                        System.out.println("Unknown command: " + commandName);
                        System.out.println("Type 'help' to see available commands");
                    }
                }
                
                System.out.println();
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection failed for command interface!");
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Print all available commands with their descriptions
     */
    private static void printAvailableCommands() {
        System.out.println("Available commands:");
        
        Map<String, ICommand> commands = CommandRegistry.getAllCommands();
        
        if (commands.isEmpty()) {
            System.out.println("No commands registered");
        } else {
            for (Map.Entry<String, ICommand> entry : commands.entrySet()) {
                String name = entry.getKey();
                String description = entry.getValue().getDescription();
                System.out.println("  " + name + " - " + description);
            }
        }
        
        System.out.println("  help - Show this help message");
        System.out.println("  exit - Exit the application");
    }
}