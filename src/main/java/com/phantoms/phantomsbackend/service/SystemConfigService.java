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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public static class LoginAccount {
        private String accountId;
        private String cookies;
        private String nickname;
        private boolean enabled;
        private long lastSignInTime;
        private String lastSignInResult;
        private boolean defaultForApi;

        private String userId;
        private String characterName;
        private String serverName;
        private String groupName;
        private String experience;
        private String avatar;
        private long userInfoUpdateTime;

        public LoginAccount() {}

        public LoginAccount(String accountId, String cookies) {
            this.accountId = accountId;
            this.cookies = cookies;
            this.nickname = accountId;
            this.enabled = true;
            this.lastSignInTime = 0;
            this.lastSignInResult = "";
            this.defaultForApi = false;
            this.userId = "";
            this.characterName = "";
            this.serverName = "";
            this.groupName = "";
            this.experience = "";
            this.avatar = "";
            this.userInfoUpdateTime = 0;
        }

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        public String getCookies() { return cookies; }
        public void setCookies(String cookies) { this.cookies = cookies; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getLastSignInTime() { return lastSignInTime; }
        public void setLastSignInTime(long lastSignInTime) { this.lastSignInTime = lastSignInTime; }
        public String getLastSignInResult() { return lastSignInResult; }
        public void setLastSignInResult(String lastSignInResult) { this.lastSignInResult = lastSignInResult; }
        public boolean isDefaultForApi() { return defaultForApi; }
        public void setDefaultForApi(boolean defaultForApi) { this.defaultForApi = defaultForApi; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getCharacterName() { return characterName; }
        public void setCharacterName(String characterName) { this.characterName = characterName; }
        public String getServerName() { return serverName; }
        public void setServerName(String serverName) { this.serverName = serverName; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public long getUserInfoUpdateTime() { return userInfoUpdateTime; }
        public void setUserInfoUpdateTime(long userInfoUpdateTime) { this.userInfoUpdateTime = userInfoUpdateTime; }
    }

    private static final String LOGIN_ACCOUNTS_KEY = "login_accounts";

    public List<LoginAccount> getLoginAccounts() {
        return getJson(LOGIN_ACCOUNTS_KEY, new TypeReference<List<LoginAccount>>() {}, new ArrayList<>());
    }

    public void saveLoginAccounts(List<LoginAccount> accounts) {
        setJsonConfig(LOGIN_ACCOUNTS_KEY, accounts, "FF14 Rising Stones 多账号登录配置");
    }

    public String addLoginAccount(String cookies) {
        List<LoginAccount> accounts = getLoginAccounts();
        String accountId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        LoginAccount newAccount = new LoginAccount(accountId, cookies);
        accounts.add(newAccount);

        saveLoginAccounts(accounts);
        logger.info("已添加新账号: {}", accountId);
        return accountId;
    }

    public String addLoginAccountWithNickname(String cookies, String nickname) {
        List<LoginAccount> accounts = getLoginAccounts();
        String accountId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        LoginAccount newAccount = new LoginAccount(accountId, cookies);
        newAccount.setNickname(nickname != null && !nickname.isEmpty() ? nickname : accountId);
        accounts.add(newAccount);

        saveLoginAccounts(accounts);
        logger.info("已添加新账号: {} ({})", nickname, accountId);
        return accountId;
    }

    public void addLoginAccountObject(LoginAccount account) {
        List<LoginAccount> accounts = getLoginAccounts();
        accounts.add(account);
        saveLoginAccounts(accounts);
        logger.info("已添加新账号(带用户信息): {} ({})", account.getNickname(), account.getAccountId());
    }

    public boolean removeLoginAccount(String accountId) {
        List<LoginAccount> accounts = getLoginAccounts();
        boolean removed = accounts.removeIf(a -> a.getAccountId().equals(accountId));

        if (removed) {
            saveLoginAccounts(accounts);
            logger.info("已删除账号: {}", accountId);
        }

        return removed;
    }

    public boolean updateLoginAccount(String accountId, String nickname, Boolean enabled) {
        List<LoginAccount> accounts = getLoginAccounts();
        for (LoginAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                if (nickname != null && !nickname.isEmpty()) {
                    account.setNickname(nickname);
                }
                if (enabled != null) {
                    account.setEnabled(enabled);
                }
                saveLoginAccounts(accounts);
                logger.info("已更新账号: {}", accountId);
                return true;
            }
        }
        return false;
    }

    public LoginAccount getLoginAccount(String accountId) {
        List<LoginAccount> accounts = getLoginAccounts();
        return accounts.stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    public void updateAccountSignInResult(String accountId, long signInTime, String result) {
        List<LoginAccount> accounts = getLoginAccounts();
        for (LoginAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                account.setLastSignInTime(signInTime);
                account.setLastSignInResult(result);
                saveLoginAccounts(accounts);
                break;
            }
        }
    }

    public List<LoginAccount> getEnabledLoginAccounts() {
        return getLoginAccounts().stream()
                .filter(LoginAccount::isEnabled)
                .collect(Collectors.toList());
    }

    public String getAnyValidLoginCookies() {
        List<LoginAccount> enabledAccounts = getEnabledLoginAccounts();
        if (enabledAccounts.isEmpty()) {
            return null;
        }
        return enabledAccounts.get(0).getCookies();
    }

    public String getDefaultApiAccountCookies() {
        List<LoginAccount> accounts = getLoginAccounts();
        for (LoginAccount account : accounts) {
            if (account.isEnabled() && account.isDefaultForApi()) {
                return account.getCookies();
            }
        }
        return getAnyValidLoginCookies();
    }

    public boolean setDefaultApiAccount(String accountId) {
        List<LoginAccount> accounts = getLoginAccounts();
        boolean found = false;
        String defaultCookies = null;
        for (LoginAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                account.setDefaultForApi(true);
                defaultCookies = account.getCookies();
                found = true;
            } else {
                account.setDefaultForApi(false);
            }
        }
        if (found) {
            saveLoginAccounts(accounts);
            if (defaultCookies != null) {
                updateLoginCookies(defaultCookies);
            }
            logger.info("已设置默认API账号: {}, cookies已同步到login_cookies", accountId);
        }
        return found;
    }

    public void migrateSingleAccountToMulti() {
        String singleCookies = getLoginCookies();
        if (singleCookies != null && !singleCookies.isEmpty()) {
            List<LoginAccount> accounts = getLoginAccounts();
            boolean hasExisting = accounts.stream()
                    .anyMatch(a -> a.getCookies() != null && a.getCookies().equals(singleCookies));

            if (!hasExisting) {
                addLoginAccountWithNickname(singleCookies, "默认账号");
                logger.info("已将单账号cookie迁移至多账号系统");
            }
        }
    }
}
