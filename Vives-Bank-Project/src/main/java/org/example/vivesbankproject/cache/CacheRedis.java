package org.example.vivesbankproject.cache;

public interface CacheRedis<K, V>{
    void save(K key, V value);
    V find(K key);
    void delete(K key);
}
