package com.example.metrics;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, lazy Singleton Metrics Registry.
 */
public final class MetricsRegistry implements Serializable {

    private static final long serialVersionUID = 1L;

    // Holds counters
    private final ConcurrentHashMap<String, Long> counters = new ConcurrentHashMap<>();

    /**
     * Reflection guard:
     * If constructor is called after instance exists -> fail.
     */
    private MetricsRegistry() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already initialized. Use getInstance().");
        }
    }

    /**
     * Lazy-loaded, thread-safe singleton via static holder.
     */
    private static class Holder {
        private static final MetricsRegistry INSTANCE = new MetricsRegistry();
    }

    public static MetricsRegistry getInstance() {
        return Holder.INSTANCE;
    }

    /* ===================== METRICS API ===================== */

    public void increment(String key) {
        counters.merge(key, 1L, Long::sum);
    }

    public void setCount(String key, long value) {
        counters.put(key, value);
    }

    public long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public Map<String, Long> getAll() {
        return Collections.unmodifiableMap(counters);
    }

    /* ===================== SERIALIZATION ===================== */

    /**
     * Ensures deserialization returns the SAME singleton instance.
     */
    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }
}