package com.phantoms.phantomsbackend.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取值序列化器 - 修复类型问题
     */
    @SuppressWarnings("unchecked")
    private RedisSerializer<Object> getValueSerializer() {
        return (RedisSerializer<Object>) redisTemplate.getValueSerializer();
    }

    /**
     * Set a key-value pair
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("Redis set error, key: {}", key, e);
            return false;
        }
    }

    /**
     * Set a key-value pair with an expiration time
     */
    public boolean setWithExpire(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            return true;
        } catch (Exception e) {
            logger.error("Redis setWithExpire error, key: {}", key, e);
            return false;
        }
    }

    /**
     * Get the value corresponding to a key
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Redis get error, key: {}", key, e);
            return null;
        }
    }

    /**
     * Delete a key
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            logger.error("Redis delete error, key: {}", key, e);
            return false;
        }
    }

    /**
     * Check if a key exists
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("Redis hasKey error, key: {}", key, e);
            return false;
        }
    }

    /**
     * Set the expiration time for a key
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
        } catch (Exception e) {
            logger.error("Redis expire error, key: {}", key, e);
            return false;
        }
    }

    /**
     * Get the remaining expiration time for a key
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            logger.error("Redis getExpire error, key: {}", key, e);
            return -2L; // -2 means key doesn't exist
        }
    }

    /**
     * Batch set key-value pairs
     */
    public boolean setAll(Map<String, Object> keyValues) {
        try {
            redisTemplate.opsForValue().multiSet(keyValues);
            return true;
        } catch (Exception e) {
            logger.error("Redis setAll error", e);
            return false;
        }
    }

    /**
     * Batch set key-value pairs with an expiration time - 专门为房屋缓存优化
     */
    public boolean setAllWithExpire(Map<String, Object> keyValues, long timeout, TimeUnit timeUnit) {
        try {
            // 使用pipeline批量操作，减少网络往返
            redisTemplate.executePipelined(new org.springframework.data.redis.core.RedisCallback<Object>() {
                @Override
                public Object doInRedis(org.springframework.data.redis.connection.RedisConnection connection) {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    RedisSerializer<Object> valueSerializer = getValueSerializer();

                    for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                        byte[] keyBytes = stringSerializer.serialize(entry.getKey());
                        byte[] valueBytes = valueSerializer.serialize(entry.getValue());
                        if (keyBytes != null && valueBytes != null) {
                            connection.setEx(keyBytes, timeUnit.toSeconds(timeout), valueBytes);
                        }
                    }
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("Redis setAllWithExpire error", e);
            // 降级方案：逐条设置
            return setAllWithExpireFallback(keyValues, timeout, timeUnit);
        }
    }

    /**
     * 降级方案：逐条设置带过期时间的键值对
     */
    private boolean setAllWithExpireFallback(Map<String, Object> keyValues, long timeout, TimeUnit timeUnit) {
        boolean allSuccess = true;
        for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
            if (!setWithExpire(entry.getKey(), entry.getValue(), timeout, timeUnit)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    /**
     * Batch get values for multiple keys
     */
    public List<Object> getAll(List<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            logger.error("Redis getAll error", e);
            return Collections.emptyList();
        }
    }

    /**
     * Batch delete multiple keys
     */
    public long deleteAll(Collection<String> keys) {
        try {
            Long deletedCount = redisTemplate.delete(keys);
            return deletedCount != null ? deletedCount : 0;
        } catch (Exception e) {
            logger.error("Redis deleteAll error", e);
            return 0;
        }
    }

    /**
     * Batch check if keys exist - 专门为房屋缓存检查优化
     */
    public Map<String, Boolean> hasKeys(Collection<String> keys) {
        Map<String, Boolean> result = new HashMap<>();
        try {
            // 使用pipeline批量检查，提高性能
            List<Object> pipelineResults = redisTemplate.executePipelined(
                new org.springframework.data.redis.core.RedisCallback<Object>() {
                    @Override
                    public Object doInRedis(org.springframework.data.redis.connection.RedisConnection connection) {
                        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                        for (String key : keys) {
                            byte[] keyBytes = stringSerializer.serialize(key);
                            if (keyBytes != null) {
                                connection.exists(keyBytes);
                            }
                        }
                        return null;
                    }
                }
            );

            // 处理批量结果
            int index = 0;
            for (String key : keys) {
                if (index < pipelineResults.size()) {
                    Object exists = pipelineResults.get(index);
                    result.put(key, Boolean.TRUE.equals(exists));
                } else {
                    result.put(key, false);
                }
                index++;
            }
        } catch (Exception e) {
            logger.error("Redis hasKeys error", e);
            // 降级方案：逐条检查
            for (String key : keys) {
                result.put(key, hasKey(key));
            }
        }
        return result;
    }

    /**
     * 专门用于设置字符串值的批量操作（房屋缓存专用）- 避免序列化问题
     */
    public boolean batchSetStringKeys(Map<String, String> keyValueMap, long timeout, TimeUnit timeUnit) {
        try {
            // 使用字符串操作，避免序列化问题
            redisTemplate.executePipelined(new org.springframework.data.redis.core.RedisCallback<Object>() {
                @Override
                public Object doInRedis(org.springframework.data.redis.connection.RedisConnection connection) {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                        byte[] keyBytes = stringSerializer.serialize(entry.getKey());
                        byte[] valueBytes = stringSerializer.serialize(entry.getValue());
                        if (keyBytes != null && valueBytes != null) {
                            connection.setEx(keyBytes, timeUnit.toSeconds(timeout), valueBytes);
                        }
                    }
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("Redis batchSetStringKeys error", e);
            return false;
        }
    }

    /**
     * 获取字符串值
     */
    public String getString(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            logger.error("Redis getString error, key: {}", key, e);
            return null;
        }
    }

    /**
     * 批量获取字符串值
     */
    public Map<String, String> getStringAll(List<String> keys) {
        Map<String, String> result = new HashMap<>();
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            if (values != null) {
                for (int i = 0; i < keys.size(); i++) {
                    if (i < values.size() && values.get(i) != null) {
                        result.put(keys.get(i), values.get(i).toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Redis getStringAll error", e);
        }
        return result;
    }

    /**
     * 房屋缓存专用方法：批量设置房屋缓存
     */
    public boolean batchCacheHouses(Map<String, String> houseCacheMap, long expireHours) {
        return batchSetStringKeys(houseCacheMap, expireHours, TimeUnit.HOURS);
    }

    /**
     * 房屋缓存专用方法：批量检查房屋缓存
     */
    public Map<String, Boolean> batchCheckHouseCache(Collection<String> houseKeys) {
        return hasKeys(houseKeys);
    }

    /**
     * 房屋缓存专用方法：清理过期房屋缓存
     */
    public long clearExpiredHouseCache(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                return deleteAll(keys);
            }
            return 0;
        } catch (Exception e) {
            logger.error("Redis clearExpiredHouseCache error, pattern: {}", pattern, e);
            return 0;
        }
    }
}