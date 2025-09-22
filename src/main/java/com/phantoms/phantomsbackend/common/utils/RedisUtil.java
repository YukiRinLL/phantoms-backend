package com.phantoms.phantomsbackend.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Set a key-value pair
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Set a key-value pair with an expiration time
    public void setWithExpire(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // Get the value corresponding to a key
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Delete a key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Check if a key exists
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // Set the expiration time for a key
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    // Get the remaining expiration time for a key
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    // Batch set key-value pairs
    public void setAll(Map<String, Object> keyValues) {
        redisTemplate.opsForValue().multiSet(keyValues);
    }

    // Batch set key-value pairs with an expiration time
    public void setAllWithExpire(Map<String, Object> keyValues, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().multiSet(keyValues);
        for (String key : keyValues.keySet()) {
            redisTemplate.expire(key, timeout, timeUnit);
        }
    }

    // Batch get values for multiple keys
    public List<Object> getAll(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // Batch delete multiple keys
    public void deleteAll(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    // Batch check if keys exist
    public Map<String, Boolean> hasKeys(Collection<String> keys) {
        Map<String, Boolean> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, redisTemplate.hasKey(key));
        }
        return result;
    }

    // Batch set expiration time for multiple keys
    public void expireAll(Collection<String> keys, long timeout, TimeUnit timeUnit) {
        for (String key : keys) {
            redisTemplate.expire(key, timeout, timeUnit);
        }
    }

    // Batch get remaining expiration time for multiple keys
    public Map<String, Long> getAllExpire(Collection<String> keys) {
        Map<String, Long> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, redisTemplate.getExpire(key));
        }
        return result;
    }
}