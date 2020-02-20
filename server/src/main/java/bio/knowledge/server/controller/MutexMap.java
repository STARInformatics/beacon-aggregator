package bio.knowledge.server.controller;

import java.util.HashMap;
import java.util.Map;

public class MutexMap {
    private Map<String, Object> map = new HashMap<>();

    private final Object mutex = new Object();

    public Object get(String id) {
        synchronized (mutex) {
            return map.computeIfAbsent(id, k -> new Object());
        }
    }
}
