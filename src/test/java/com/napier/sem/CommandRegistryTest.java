package com.napier.sem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandRegistry automatic command discovery
 */
class CommandRegistryTest {

    @BeforeAll
    static void setUp() {
        CommandRegistry.initializeCommands();
    }

    @Test
    @DisplayName("Should automatically discover and register all commands")
    void testAutomaticCommandDiscovery() {
        Map<String, ICommand> allCommands = CommandRegistry.getAllCommands();
        
        assertNotNull(allCommands, "Command map should not be null");
        assertFalse(allCommands.isEmpty(), "Should have discovered and registered commands");
        
        // We know we have at least these commands based on the codebase
        assertTrue(allCommands.size() >= 16, 
            "Should have discovered at least 16 commands, found: " + allCommands.size());
    }

    @Test
    @DisplayName("Should register CapitalCitiesByContinentCommand")
    void testCapitalCitiesByContinentCommandRegistered() {
        assertTrue(CommandRegistry.hasCommand("capital-cities-continent"),
            "CapitalCitiesByContinentCommand should be automatically registered");
        
        ICommand command = CommandRegistry.getCommand("capital-cities-continent");
        assertNotNull(command, "Command should be retrievable");
        assertEquals("capital-cities-continent", command.getExcecutionCommand());
    }

    @Test
    @DisplayName("Should register TopCountriesCommand")
    void testTopCountriesCommandRegistered() {
        assertTrue(CommandRegistry.hasCommand("top-countries"),
            "TopCountriesCommand should be automatically registered");
    }

    @Test
    @DisplayName("Should register TopCitiesCommand")
    void testTopCitiesCommandRegistered() {
        assertTrue(CommandRegistry.hasCommand("top-cities"),
            "TopCitiesCommand should be automatically registered");
    }

    @Test
    @DisplayName("Should register CapitalCitiesByRegionCommand")
    void testCapitalCitiesByRegionCommandRegistered() {
        assertTrue(CommandRegistry.hasCommand("capital-cities-region"),
            "CapitalCitiesByRegionCommand should be automatically registered");
    }

    @Test
    @DisplayName("Should handle case-insensitive command lookup")
    void testCaseInsensitiveCommandLookup() {
        assertTrue(CommandRegistry.hasCommand("CAPITAL-CITIES-CONTINENT"),
            "Command lookup should be case-insensitive");
        
        assertTrue(CommandRegistry.hasCommand("Capital-Cities-Continent"),
            "Command lookup should be case-insensitive");
    }

    @Test
    @DisplayName("Should return null for non-existent command")
    void testNonExistentCommand() {
        assertFalse(CommandRegistry.hasCommand("non-existent-command"),
            "Should return false for non-existent command");
        
        assertNull(CommandRegistry.getCommand("non-existent-command"),
            "Should return null for non-existent command");
    }

    @Test
    @DisplayName("All registered commands should have valid execution commands")
    void testAllCommandsHaveValidExecutionCommands() {
        Map<String, ICommand> allCommands = CommandRegistry.getAllCommands();
        
        for (Map.Entry<String, ICommand> entry : allCommands.entrySet()) {
            ICommand command = entry.getValue();
            assertNotNull(command.getExcecutionCommand(), 
                "Command should have execution command");
            assertFalse(command.getExcecutionCommand().isEmpty(), 
                "Execution command should not be empty");
        }
    }

    @Test
    @DisplayName("All registered commands should have descriptions")
    void testAllCommandsHaveDescriptions() {
        Map<String, ICommand> allCommands = CommandRegistry.getAllCommands();
        
        for (Map.Entry<String, ICommand> entry : allCommands.entrySet()) {
            ICommand command = entry.getValue();
            assertNotNull(command.getDescription(), 
                "Command " + command.getExcecutionCommand() + " should have description");
            assertFalse(command.getDescription().isEmpty(), 
                "Description for " + command.getExcecutionCommand() + " should not be empty");
        }
    }
}
