package com.hcmus.mela.shared.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SnakeToCamelConverter {
    public static Object convert(Object input) {
        if (input instanceof Map<?, ?>) {
            return convertMap((Map<?, ?>) input);
        } else if (input instanceof List<?>) {
            return convertList((List<?>) input);
        }
        return input;
    }

    private static Map<String, Object> convertMap(Map<?, ?> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                String camelKey = toCamelCase((String) key);
                result.put(camelKey, convert(entry.getValue()));
            }
        }
        return result;
    }

    private static List<Object> convertList(List<?> input) {
        List<Object> result = new ArrayList<>();
        for (Object item : input) {
            result.add(convert(item));
        }
        return result;
    }

    private static String toCamelCase(String snake) {
        String[] parts = snake.split("_");
        StringBuilder camel = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camel.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }
        return camel.toString();
    }
}

