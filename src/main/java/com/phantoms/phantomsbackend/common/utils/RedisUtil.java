package com.phantoms.phantomsbackend.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
}