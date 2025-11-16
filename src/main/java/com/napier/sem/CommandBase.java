package com.napier.sem;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base class for database commands.
 * Provides common implementation for command properties.
 */
public abstract class CommandBase implements ICommand {
    
    /**
     * Execution command string - what the user types to execute the command
     */
    protected String executionCommand;
    
    /**
     * Description of the command
     */
    protected String description;
    
    /**
     * Constructor for CommandBase
     * @param executionCommand The command string to execute this command
     * @param description Description of what the command does
     */
    public CommandBase(String executionCommand, String description) {
        this.executionCommand = executionCommand;
        this.description = description;
    }
    
    /**
     * Get the execution command string
     * @return Execution command string
     */
    @Override
    public String getExcecutionCommand() {
        return executionCommand;
    }
    
    /**
     * Get the description of the command
     * @return Command description
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Execute the command with given arguments.
     * Must be implemented by concrete command classes.
     * @param connection Database connection.
     * @param args Command arguments.
     * @throws SQLException if database operation fails.
     */
    @Override
    public abstract void execute(Connection connection, String[] args) throws SQLException;
}
