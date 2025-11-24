package ru.otus.hw.processor;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private final Map<String, Map<Long, String>> cache = new HashMap<>();

    public void put(String entityName, Long postgresId, String mongoId) {
        cache.computeIfAbsent(entityName, newMap -> new HashMap<>())
            .put(postgresId, mongoId);
    }

    public String get(String entityName, Long postgresId) {
        return cache.get(entityName).get(postgresId);
    }
}
