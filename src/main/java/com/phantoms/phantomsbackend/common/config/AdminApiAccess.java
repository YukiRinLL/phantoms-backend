package com.phantoms.phantomsbackend.common.config;

import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminApiAccess {

    /**
     * 管理密码在 system_config 表中的配置项 key。
     * 配置后以数据库密码为准；未配置时回退到环境变量 app.admin-api-key。
     */
    public static final String ADMIN_KEY_CONFIG = "system.admin-key";

    private final String envAdminApiKey;
    private final SystemConfigService systemConfigService;

    public AdminApiAccess(@Value("${app.admin-api-key:}") String envAdminApiKey,
                          SystemConfigService systemConfigService) {
        this.envAdminApiKey = envAdminApiKey;
        this.systemConfigService = systemConfigService;
    }

    public boolean isAllowed(String suppliedKey) {
        if (suppliedKey == null) {
            return false;
        }
        String dbKey = systemConfigService.getString(ADMIN_KEY_CONFIG, "");
        String effectiveKey = !dbKey.isBlank() ? dbKey : envAdminApiKey;
        return !effectiveKey.isBlank() && effectiveKey.equals(suppliedKey);
    }
}
