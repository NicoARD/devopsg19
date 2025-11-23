package com.napier.sem;

import com.napier.sem.commands.region.TopCapitalCitiesByRegionCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopCapitalCitiesByRegionCommandTest {

    @Test
    void testInvalidArgs() {
        TopCapitalCitiesByRegionCommand cmd = new TopCapitalCitiesByRegionCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{}));
    }

    @Test
    void testInvalidNumber() {
        TopCapitalCitiesByRegionCommand cmd = new TopCapitalCitiesByRegionCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{"Europe", "XYZ"}));
    }

    @Test
    void testValidInputDoesNotThrow() {
        TopCapitalCitiesByRegionCommand cmd = new TopCapitalCitiesByRegionCommand();
        assertDoesNotThrow(() -> cmd.execute(new String[]{"Asia", "5"}));
    }
}
