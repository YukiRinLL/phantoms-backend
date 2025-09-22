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
}