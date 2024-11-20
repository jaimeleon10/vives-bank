package org.example.vivesbankproject.common;

public interface Cache <K, V>{
    void save(K key, V value);
    V find(K key);
    void delete(K key);
}
