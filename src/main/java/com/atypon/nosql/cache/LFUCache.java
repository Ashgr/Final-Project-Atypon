package com.atypon.nosql.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LFUCache<K, V> implements Cache<K, V> {

    private static LFUCache cache;
    private final int CAPACITY = 10;
    private ConcurrentHashMap<K, Unit> map;

    private LFUCache() {
        map = new ConcurrentHashMap<>();
    }

    public static LFUCache getInstance() {
        if (cache == null) {
            cache = new LFUCache();
        }
        return cache;
    }

    @Override
    public V get(K key) {
        if (map.containsKey(key)) {
            System.out.println("Query in cache");
            return map.get(key).getObject();
        }
        System.out.println("Query not in cache");
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        if (value == null) {
            throw new NullPointerException("Value must not be null");
        }
        synchronized (this) {
            if (map.size() == CAPACITY) {
                map.remove(getMinFreqKey());
            }
            map.put(key, new Unit(value));
        }
    }

    @Override
    public void remove(K key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        synchronized (this) {
            map.remove(key);
        }
    }

    private K getMinFreqKey() {
        K key = null;
        int minFrequency = Integer.MAX_VALUE;
        for (Map.Entry<K, Unit> entry : map.entrySet()) {
            if (entry.getValue().getFrequency() < minFrequency) {
                key = entry.getKey();
                minFrequency = entry.getValue().getFrequency();
            }
        }
        return key;
    }

    private class Unit {
        private V object;
        private int frequency;

        Unit(V object) {
            this.object = object;
            frequency = 0;
        }

        V getObject() {
            ++frequency;
            if (frequency == Integer.MAX_VALUE) {
                for (Map.Entry<K, Unit> entry : map.entrySet()) {
                    entry.getValue().decreaseFrequency();
                }
            }
            return object;
        }

        int getFrequency() {
            return frequency;
        }

        void decreaseFrequency() {
            frequency /= 2;
        }
    }
}
