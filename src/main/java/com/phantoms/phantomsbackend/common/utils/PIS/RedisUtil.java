//package com.phantoms.phantomsbackend.common.utils.PIS;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RedisUtil {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//
//    //写入对象
//    public boolean setObject(final String key, Object value, Integer expireTime) {
//        try {
//
//            redisTemplate.opsForValue().set(key, value);
//            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    //获取对象
//    public Object getObject(final String key) {
//        return key == null ? null : redisTemplate.opsForValue().get(key);
//    }
//
//
//    //写入集合
//    public boolean setList(final String key, Object value, Integer expireTime) {
//        try {
//            redisTemplate.opsForList().rightPush(key, value);
//            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    //获取集合
//    public List<Object> getList(final String key) {
//        try {
//            return redisTemplate.opsForList().range(key, 0, -1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    //判断时候存在key
//    public boolean hasKey(final String key) {
//        try {
//            return redisTemplate.hasKey(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    //删除key
//    public void del(final String key) {
//        if (hasKey(key)) {
//            redisTemplate.delete(key);
//        }
//    }
//
//
//}