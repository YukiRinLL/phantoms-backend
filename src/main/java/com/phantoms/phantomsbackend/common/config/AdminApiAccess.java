package com.phantoms.phantomsbackend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminApiAccess {
    private final String adminApiKey;

    public AdminApiAccess(@Value("${app.admin-api-key:}") String adminApiKey) {
        this.adminApiKey = adminApiKey;
    }

    public boolean isAllowed(String suppliedKey) {
        return suppliedKey != null && !adminApiKey.isBlank() && adminApiKey.equals(suppliedKey);
    }
}
