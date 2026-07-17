package com.phantoms.phantomsbackend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.phantoms.phantomsbackend.pojo.entity.primary.SystemConfig;
import com.phantoms.phantomsbackend.repository.primary.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SystemConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigService.class);

    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @PostConstruct
    public void loadConfig() {
        logger.info("开始加载系统配置到内存");
        try {
            List<SystemConfig> configs = systemConfigRepository.findAll();
            configCache.clear();
            configs.forEach(c -> configCache.put(c.getKey(), c.getValue()));
            logger.info("系统配置加载完成，共 {} 条", configCache.size());
        } catch (Exception e) {
            logger.error("加载系统配置失败", e);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void refreshConfig() {
        logger.debug("开始刷新系统配置");
        try {
            List<SystemConfig> configs = systemConfigRepository.findAll();
            ConcurrentHashMap<String, String> newCache = new ConcurrentHashMap<>();
            configs.forEach(c -> newCache.put(c.getKey(), c.getValue()));
            configCache.clear();
            configCache.putAll(newCache);
            logger.debug("系统配置刷新完成，共 {} 条", configCache.size());
        } catch (Exception e) {
            logger.error("刷新系统配置失败", e);
        }
    }

    public String getString(String key) {
        return configCache.get(key);
    }

    public String getString(String key, String defaultValue) {
        String value = configCache.get(key);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = configCache.get(key);
        if (value != null && !value.isEmpty()) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = configCache.get(key);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("配置项 {} 的值 {} 无法转换为整数", key, value);
            }
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        String value = configCache.get(key);
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.warn("配置项 {} 的值 {} 无法转换为长整数", key, value);
            }
        }
        return defaultValue;
    }

    public <T> T getJson(String key, Class<T> type, T defaultValue) {
        String value = configCache.get(key);
        if (value != null && !value.isEmpty()) {
            try {
                return JSON.parseObject(value, type);
            } catch (Exception e) {
                logger.warn("配置项 {} 的值 {} 无法解析为 JSON", key, value);
            }
        }
        return defaultValue;
    }

    public <T> T getJson(String key, TypeReference<T> typeReference, T defaultValue) {
        String value = configCache.get(key);
        if (value != null && !value.isEmpty()) {
            try {
                return JSON.parseObject(value, typeReference);
            } catch (Exception e) {
                logger.warn("配置项 {} 的值 {} 无法解析为 JSON", key, value);
            }
        }
        return defaultValue;
    }

    public void setConfig(String key, String value, String description) {
        Optional<SystemConfig> configOptional = systemConfigRepository.findByKey(key);
        if (configOptional.isPresent()) {
            SystemConfig config = configOptional.get();
            config.setValue(value);
            if (description != null && !description.isEmpty()) {
                config.setDescription(description);
            }
            systemConfigRepository.save(config);
        } else {
            SystemConfig config = new SystemConfig();
            config.setKey(key);
            config.setValue(value);
            config.setDescription(description != null ? description : "");
            systemConfigRepository.save(config);
        }
        configCache.put(key, value);
        logger.info("配置项 {} 已更新", key);
    }

    public void setJsonConfig(String key, Object value, String description) {
        String jsonValue = JSON.toJSONString(value);
        setConfig(key, jsonValue, description);
    }

    public void deleteConfig(String key) {
        systemConfigRepository.deleteById(key);
        configCache.remove(key);
        logger.info("配置项 {} 已删除", key);
    }

    public Map<String, String> getAllConfigs() {
        return new ConcurrentHashMap<>(configCache);
    }

    public boolean hasConfig(String key) {
        return configCache.containsKey(key) && !configCache.get(key).isEmpty();
    }

    public String getDaoYuKey() {
        return getString("daoyu_key", "default-key");
    }

    public void updateDaoYuKey(String newKey) {
        setConfig("daoyu_key", newKey, "FF14 DaoYu Key");
    }

    public String getLoginCookies() {
        return getString("login_cookies");
    }

    public void updateLoginCookies(String newCookies) {
        setConfig("login_cookies", newCookies, "FF14 Rising Stones 登录Cookies");
    }

    public String getDaoyuToken() {
        return getString("daoyu_token");
    }

    public void updateDaoyuToken(String newToken) {
        setConfig("daoyu_token", newToken, "FF14 Rising Stones Daoyu Token");
    }

    public long getTokenObtainTime() {
        return getLong("token_obtain_time", 0);
    }

    public void updateTokenObtainTime(long newTime) {
        setConfig("token_obtain_time", String.valueOf(newTime), "FF14 Rising Stones Token Obtain Time");
    }
}
