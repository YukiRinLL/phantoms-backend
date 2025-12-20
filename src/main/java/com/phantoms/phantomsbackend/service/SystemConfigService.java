package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.SystemConfig;
import com.phantoms.phantomsbackend.repository.primary.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public String getDaoYuKey() {
        Optional<SystemConfig> config = systemConfigRepository.findByKey("daoyu_key");
        return config.map(SystemConfig::getValue).orElse("default-key");
    }

    public void updateDaoYuKey(String newKey) {
        // 检查是否已经存在该配置
        Optional<SystemConfig> configOptional = systemConfigRepository.findByKey("daoyu_key");
        if (configOptional.isPresent()) {
            // 如果存在，直接更新 value
            systemConfigRepository.updateByKey("daoyu_key", newKey, configOptional.get().getDescription());
        } else {
            // 如果不存在，创建一个新的 SystemConfig 实体并保存
            SystemConfig config = new SystemConfig();
            config.setKey("daoyu_key");
            config.setValue(newKey);
            config.setDescription("Description for daoyu_key");
            systemConfigRepository.save(config);
        }
    }

    /**
     * 获取登录Cookies
     */
    public String getLoginCookies() {
        Optional<SystemConfig> config = systemConfigRepository.findByKey("login_cookies");
        return config.map(SystemConfig::getValue).orElse(null);
    }

    /**
     * 更新登录Cookies
     */
    public void updateLoginCookies(String newCookies) {
        // 检查是否已经存在该配置
        Optional<SystemConfig> configOptional = systemConfigRepository.findByKey("login_cookies");
        if (configOptional.isPresent()) {
            // 如果存在，直接更新 value
            systemConfigRepository.updateByKey("login_cookies", newCookies, configOptional.get().getDescription());
        } else {
            // 如果不存在，创建一个新的 SystemConfig 实体并保存
            SystemConfig config = new SystemConfig();
            config.setKey("login_cookies");
            config.setValue(newCookies);
            config.setDescription("FF14 Rising Stones 登录Cookies");
            systemConfigRepository.save(config);
        }
    }

    /**
     * 获取Daoyu Token
     */
    public String getDaoyuToken() {
        Optional<SystemConfig> config = systemConfigRepository.findByKey("daoyu_token");
        return config.map(SystemConfig::getValue).orElse(null);
    }

    /**
     * 更新Daoyu Token
     */
    public void updateDaoyuToken(String newToken) {
        // 检查是否已经存在该配置
        Optional<SystemConfig> configOptional = systemConfigRepository.findByKey("daoyu_token");
        if (configOptional.isPresent()) {
            // 如果存在，直接更新 value
            systemConfigRepository.updateByKey("daoyu_token", newToken, configOptional.get().getDescription());
        } else {
            // 如果不存在，创建一个新的 SystemConfig 实体并保存
            SystemConfig config = new SystemConfig();
            config.setKey("daoyu_token");
            config.setValue(newToken);
            config.setDescription("FF14 Rising Stones Daoyu Token");
            systemConfigRepository.save(config);
        }
    }
    
    /**
     * 获取Token获取时间
     */
    public long getTokenObtainTime() {
        Optional<SystemConfig> config = systemConfigRepository.findByKey("token_obtain_time");
        if (config.isPresent()) {
            try {
                return Long.parseLong(config.get().getValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * 更新Token获取时间
     */
    public void updateTokenObtainTime(long newTime) {
        String timeStr = String.valueOf(newTime);
        // 检查是否已经存在该配置
        Optional<SystemConfig> configOptional = systemConfigRepository.findByKey("token_obtain_time");
        if (configOptional.isPresent()) {
            // 如果存在，直接更新 value
            systemConfigRepository.updateByKey("token_obtain_time", timeStr, configOptional.get().getDescription());
        } else {
            // 如果不存在，创建一个新的 SystemConfig 实体并保存
            SystemConfig config = new SystemConfig();
            config.setKey("token_obtain_time");
            config.setValue(timeStr);
            config.setDescription("FF14 Rising Stones Token Obtain Time");
            systemConfigRepository.save(config);
        }
    }
}