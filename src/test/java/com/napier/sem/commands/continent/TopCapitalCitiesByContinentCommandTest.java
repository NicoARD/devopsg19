package com.napier.sem.commands.continent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopCapitalCitiesByContinentCommandTest {

    @Test
    void testMissingArguments() {
        TopCapitalCitiesByContinentCommand cmd = new TopCapitalCitiesByContinentCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{}));
    }

    @Test
    void testInvalidNumberInput() {
        TopCapitalCitiesByContinentCommand cmd = new TopCapitalCitiesByContinentCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{"Asia", "abc"}));
    }

    @Test
    void testValidInput() {
        TopCapitalCitiesByContinentCommand cmd = new TopCapitalCitiesByContinentCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{"Europe", "5"}));
    }
}
