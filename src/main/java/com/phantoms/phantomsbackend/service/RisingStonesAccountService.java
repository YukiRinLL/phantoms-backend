package com.phantoms.phantomsbackend.service;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.pojo.entity.primary.RisingStonesAccount;
import com.phantoms.phantomsbackend.repository.primary.RisingStonesAccountRepository;
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
            account.setLastSignInDetail(signInResult);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void updateAccountSignInStatus(String accountId, String status, String detail, String rawResponse) {
        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            account.setLastSignInTime(System.currentTimeMillis());
            account.setLastSignInStatus(status);
            account.setLastSignInDetail(detail);
            account.setLastSignInRawResponse(rawResponse);
            accountRepository.save(account);
        }
    }

    @Transactional
    public void updateAccountRewardStatus(String accountId, String status, String detail, String rawResponse) {
        RisingStonesAccount account = getAccount(accountId);
        if (account != null) {
            account.setLastRewardTime(System.currentTimeMillis());
            account.setLastRewardStatus(status);
            account.setLastRewardDetail(detail);
            account.setLastRewardRawResponse(rawResponse);
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
