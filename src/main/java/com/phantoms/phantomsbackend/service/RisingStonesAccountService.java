package com.phantoms.phantomsbackend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.pojo.entity.primary.RisingStonesAccount;
import com.phantoms.phantomsbackend.repository.primary.RisingStonesAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RisingStonesAccountService {

    private final RisingStonesAccountRepository accountRepository;
    private final SystemConfigService systemConfigService;

    @PostConstruct
    public void init() {
        migrateAccountsFromConfig();
    }

    private void migrateAccountsFromConfig() {
        try {
            String accountsJson = systemConfigService.getConfig("login_accounts");
            if (accountsJson != null && !accountsJson.isEmpty()) {
                List<RisingStonesAccount> existingAccounts = accountRepository.findAll();
                if (existingAccounts.isEmpty()) {
                    log.info("开始从配置表迁移账号数据...");
                    JSONArray accountsArray = JSON.parseArray(accountsJson);
                    for (int i = 0; i < accountsArray.size(); i++) {
                        JSONObject accountJson = accountsArray.getJSONObject(i);
                        RisingStonesAccount account = new RisingStonesAccount();
                        account.setAccountId(accountJson.getString("accountId"));
                        account.setCookies(accountJson.getString("cookies"));
                        account.setNickname(accountJson.getString("nickname"));
                        account.setEnabled(accountJson.getBooleanValue("enabled"));
                        account.setDefaultForApi(accountJson.getBooleanValue("defaultForApi"));
                        account.setUserId(accountJson.getString("userId"));
                        account.setCharacterName(accountJson.getString("characterName"));
                        account.setServerName(accountJson.getString("serverName"));
                        account.setGroupName(accountJson.getString("groupName"));
                        account.setAvatar(accountJson.getString("avatar"));
                        account.setExperience(accountJson.getString("experience"));
                        account.setLastSignInTime(accountJson.getLong("lastSignInTime"));
                        account.setLastSignInResult(accountJson.getString("lastSignInResult"));
                        account.setUserInfoUpdateTime(accountJson.getLong("userInfoUpdateTime"));
                        accountRepository.save(account);
                    }
                    log.info("账号数据迁移完成，共迁移 {} 条", accountsArray.size());
                }
            }
        } catch (Exception e) {
            log.error("迁移账号数据失败", e);
        }
    }

    public List<RisingStonesAccount> getAllAccounts() {
        return accountRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<RisingStonesAccount> getEnabledAccounts() {
        return accountRepository.findByEnabledTrue();
    }

    public RisingStonesAccount getAccount(String accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    @Transactional
    public RisingStonesAccount saveAccount(RisingStonesAccount account) {
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String accountId) {
        accountRepository.deleteByAccountId(accountId);
    }

    @Transactional
    public void updateAccountSignInResult(String accountId, Long signInTime, String signInResult) {
        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            account.setLastSignInTime(signInTime);
            account.setLastSignInResult(signInResult);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void toggleAccountEnabled(String accountId, boolean enabled) {
        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            account.setEnabled(enabled);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void setDefaultApiAccount(String accountId) {
        accountRepository.findByDefaultForApiTrue().ifPresent(account -> {
            account.setDefaultForApi(false);
            accountRepository.save(account);
        });

        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            account.setDefaultForApi(true);
            accountRepository.save(account);
        }
    }

    public RisingStonesAccount getDefaultApiAccount() {
        return accountRepository.findByDefaultForApiTrue().orElse(null);
    }

    @Transactional
    public void updateAccountUserInfo(String accountId, String characterName, String serverName,
                                      String groupName, String avatar, String experience, String userId) {
        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            if (characterName != null) account.setCharacterName(characterName);
            if (serverName != null) account.setServerName(serverName);
            if (groupName != null) account.setGroupName(groupName);
            if (avatar != null) account.setAvatar(avatar);
            if (experience != null) account.setExperience(experience);
            if (userId != null) account.setUserId(userId);
            account.setUserInfoUpdateTime(System.currentTimeMillis());
            accountRepository.save(account);
        }
    }
}
