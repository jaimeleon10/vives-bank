package org.example.vivesbankproject.cache;

import org.example.vivesbankproject.common.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService <K,V> implements Cache<K,V> {

    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    @Override
    public void save(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public V find(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(K key) {
        redisTemplate.delete(key);
    }

}
