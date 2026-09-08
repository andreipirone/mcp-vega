package com.andrei.mcpvega.model;

import java.util.Arrays;
import java.util.Optional;

public enum ChartType {
    BAR("bar"),
    LINE("line"),
    PIE("pie");

    private final String key;

    ChartType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static Optional<ChartType> fromKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        String normalized = key.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(ct -> ct.key.equals(normalized))
                .findFirst();
    }

    public static String supportedKeys() {
        return Arrays.stream(values())
                .map(ChartType::key)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}
