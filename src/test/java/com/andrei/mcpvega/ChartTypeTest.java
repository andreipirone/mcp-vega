package com.andrei.mcpvega;

import com.andrei.mcpvega.model.ChartType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartTypeTest {

    @Test
    void parsesExactKey() {
        Assertions.assertEquals(Optional.of(ChartType.BAR), ChartType.fromKey("bar"));
        assertEquals(Optional.of(ChartType.LINE), ChartType.fromKey("line"));
        assertEquals(Optional.of(ChartType.PIE), ChartType.fromKey("pie"));
    }

    @Test
    void parsesCaseInsensitive() {
        assertEquals(Optional.of(ChartType.BAR), ChartType.fromKey("BAR"));
        assertEquals(Optional.of(ChartType.PIE), ChartType.fromKey("Pie"));
    }

    @Test
    void trimsWhitespace() {
        assertEquals(Optional.of(ChartType.LINE), ChartType.fromKey("  line  "));
    }

    @Test
    void rejectsUnknown() {
        assertTrue(ChartType.fromKey("scatter").isEmpty());
        assertTrue(ChartType.fromKey("bar.json").isEmpty());
        assertTrue(ChartType.fromKey("").isEmpty());
        assertTrue(ChartType.fromKey(null).isEmpty());
    }

    @Test
    void supportedKeysListsAll() {
        String keys = ChartType.supportedKeys();
        assertTrue(keys.contains("bar"));
        assertTrue(keys.contains("line"));
        assertTrue(keys.contains("pie"));
        assertFalse(keys.contains("["));
    }
}
