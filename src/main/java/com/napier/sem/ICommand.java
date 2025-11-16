package com.napier.sem;

import java.sql.Connection;
import java.sql.SQLException;
//
/**
 * Interface for database commands
 */
public interface ICommand {

    /**
     * Execution command string, 
     * what the user types to execute the command
     */
    public String excecutionCommand = "";
    
    /**
     * Description of the command
     */
    public String description = "";
    
    /**
     * Get the execution command string
     * @return Execution command string
     */
    String getExcecutionCommand();
    
    /**
     * Get the description of the command
     * @return Command description
     */
    String getDescription();

    /**
     * Execute the command with given arguments.
     * @param connection Database connection.
     * @param args Command arguments.
     * @throws SQLException if database operation fails.
     */
    void execute(Connection connection, String[] args) throws SQLException;
}