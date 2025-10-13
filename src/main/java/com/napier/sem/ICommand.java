package com.napier.sem;

import java.sql.Connection;

/**
 * Interface for database commands
 */
public interface ICommand {
    
    /**
     * Description of the command
     */
    public String description = "";
    
    /**
     * Get the description of the command
     * @return Command description
     */
    String getDescription();
    
    /**
     * Execute the command with given arguments
     * @param connection Database connection
     * @param args Command arguments
     */
    void execute(Connection connection, String[] args);
}