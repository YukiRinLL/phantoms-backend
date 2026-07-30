package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import com.phantoms.phantomsbackend.pojo.entity.primary.RisingStonesAccount;
import com.phantoms.phantomsbackend.service.RisingStonesAccountService;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DailySignInScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailySignInScheduler.class);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RisingStonesUtils risingStonesUtils;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RisingStonesAccountService risingStonesAccountService;

    private String getAdminQQ() {
        return systemConfigService.getString("napcat.admin-qq", "944989026");
    }

    private boolean isSignInEnabled() {
        return systemConfigService.getBoolean("scheduler.signin.enabled", true);
    }

    /**
     * 每日签到任务 - UTC+8每天00:05执行
     * 先执行签到，然后尝试领取可用奖励
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void dailySignInTask() {
        if (!isSignInEnabled()) {
            logger.info("每日签到任务已禁用");
            return;
        }

        logger.info("开始执行每日签到任务 - {}", LocalDateTime.now().format(DATE_FORMATTER));
        
        try {
            List<RisingStonesAccount> enabledAccounts = risingStonesAccountService.getEnabledAccounts();
            
            if (enabledAccounts.isEmpty()) {
                logger.warn("没有启用的账号，跳过签到任务");
                return;
            }

            logger.info("共有 {} 个启用账号需要签到", enabledAccounts.size());
            
            for (RisingStonesAccount account : enabledAccounts) {
                try {
                    logger.info("开始为账号 {} 执行签到", account.getAccountId());
                    logger.info("账号 {} cookies 长度: {}", account.getAccountId(), 
                        account.getCookies() != null ? account.getCookies().length() : 0);
                    
                    JSONObject signInResult = risingStonesUtils.doSignIn(account.getCookies());
                    String rawResponse = signInResult != null ? signInResult.toJSONString() : "null";
                   
                    logger.info("账号 {} 签到响应: {}", account.getAccountId(), 
                        rawResponse.length() > 200 ? rawResponse.substring(0, 200) + "..." : rawResponse);
                    
                    if (signInResult != null && signInResult.getInteger("code") == 10001) {
                        String message = getApiMessage(signInResult);
                        logger.info("账号 {} 签到成功 - {}", account.getAccountId(), message);
                        risingStonesAccountService.updateAccountSignInStatus(
                            account.getAccountId(), "SUCCESS", message, rawResponse);
                        
                        claimAvailableRewards(account.getCookies(), account.getAccountId());
                        
                        sendNotification("✅ 每日签到任务执行成功", 
                            "账号: " + account.getNickname() + "\n签到结果: " + message);
                    } else {
                        String errorMsg = getApiMessage(signInResult);
                        logger.error("账号 {} 签到失败: {}", account.getAccountId(), errorMsg);
                        String statusDetail = "签到失败: " + errorMsg + " (code: " + (signInResult != null ? signInResult.getInteger("code") : "null") + ")";
                        risingStonesAccountService.updateAccountSignInStatus(
                            account.getAccountId(), "FAILED", statusDetail, rawResponse);
                        sendNotification("❌ 每日签到任务执行失败", 
                            "账号: " + account.getNickname() + "\n签到失败: " + errorMsg);
                    }
                } catch (IOException e) {
                    logger.error("账号 {} 签到异常", account.getAccountId(), e);
                    String errorDetail = "签到异常: " + e.getMessage();
                    risingStonesAccountService.updateAccountSignInStatus(
                        account.getAccountId(), "ERROR", errorDetail, null);
                    sendNotification("❌ 每日签到任务执行异常", 
                        "账号: " + account.getNickname() + "\n异常信息: " + e.getMessage());
                } catch (Exception e) {
                    logger.error("账号 {} 签到过程发生未知异常", account.getAccountId(), e);
                    String errorDetail = "签到异常: " + e.getMessage();
                    risingStonesAccountService.updateAccountSignInStatus(
                        account.getAccountId(), "ERROR", errorDetail, null);
                }
            }
        } catch (Exception e) {
            logger.error("每日签到任务执行发生未知异常", e);
            sendNotification("❌ 每日签到任务执行发生未知异常", 
                "异常信息: " + e.getMessage());
        }
    }

    /**
     * 领取可用的签到奖励
     */
    public void claimAvailableRewards(String cookies, String accountId) {
        logger.info("开始尝试领取签到奖励，账号: {}", accountId);
        
        try {
            String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
            
            JSONObject rewardListResult = risingStonesUtils.getSignInRewardList(cookies, currentMonth);
            String rewardListRaw = rewardListResult != null ? rewardListResult.toJSONString() : "null";
            
            StringBuilder allRawResponses = new StringBuilder();
            allRawResponses.append("=== 奖励列表 ===\n");
            allRawResponses.append(rewardListRaw).append("\n\n");
            
            logger.info("账号 {} getSignInRewardList 返回: {}", accountId, 
                rewardListRaw.length() > 300 ? rewardListRaw.substring(0, 300) + "..." : rewardListRaw);
            
            if (rewardListResult != null && rewardListResult.getInteger("code") != null && rewardListResult.getInteger("code") == 10000) {
                JSONArray rewardList = rewardListResult.getJSONArray("data");
                
                if (rewardList != null && !rewardList.isEmpty()) {
                    logger.info("获取到 {} 个签到奖励", rewardList.size());
                    
                    StringBuilder rewardSummary = new StringBuilder();
                    int successCount = 0;
                    int failCount = 0;
                    int availableCount = 0;
                    int notAvailableCount = 0;
                    
                    for (int i = 0; i < rewardList.size(); i++) {
                        JSONObject reward = rewardList.getJSONObject(i);
                        
                        Integer isGet = reward.getInteger("is_get");
                        if (isGet != null && isGet == 0) {
                            availableCount++;
                            Integer rewardId = reward.getInteger("id");
                            String rewardName = reward.getString("item_name");
                            
                            allRawResponses.append("=== 领取奖励: ").append(rewardName).append(" (ID: ").append(rewardId).append(") ===\n");
                            
                            try {
                                JSONObject claimResult = risingStonesUtils.getSignInReward(cookies, rewardId, currentMonth);
                                String claimRaw = claimResult != null ? claimResult.toJSONString() : "null";
                                allRawResponses.append(claimRaw).append("\n\n");
                                
                                if (claimResult != null && claimResult.getInteger("code") != null && claimResult.getInteger("code") == 10000) {
                                    logger.info("账号 {} 成功领取奖励: {} (ID: {})", accountId, rewardName, rewardId);
                                    rewardSummary.append("✅").append(rewardName).append(" ");
                                    successCount++;
                                } else {
                                    String errorMsg = getApiMessage(claimResult);
                                    logger.error("账号 {} 领取奖励失败: {} (ID: {}), 错误信息: {}", accountId, rewardName, rewardId, errorMsg);
                                    rewardSummary.append("❌").append(rewardName).append("(").append(errorMsg).append(") ");
                                    failCount++;
                                }
                            } catch (IOException e) {
                                logger.error("账号 {} 领取奖励时发生异常: {} (ID: {})", accountId, rewardName, rewardId, e);
                                rewardSummary.append("❌").append(rewardName).append("(异常) ");
                                failCount++;
                                allRawResponses.append("异常: ").append(e.getMessage()).append("\n\n");
                            }
                        } else {
                            notAvailableCount++;
                        }
                    }
                    
                    String rewardStatus;
                    if (successCount > 0 && failCount == 0) {
                        rewardStatus = "SUCCESS";
                    } else if (successCount > 0 && failCount > 0) {
                        rewardStatus = "PARTIAL";
                    } else if (availableCount == 0 && notAvailableCount > 0) {
                        rewardStatus = "NO_REWARD";
                    } else {
                        rewardStatus = "FAILED";
                    }
                    String rewardDetail = String.format("共 %d 个奖励, 可领取 %d 个, 已领取 %d 个成功, %d 个失败", 
                        rewardList.size(), availableCount, successCount, failCount);
                    if (availableCount == 0 && notAvailableCount > 0) {
                        rewardDetail += " (未达成签到天数)";
                    }
                    if (rewardSummary.length() > 500) {
                        rewardDetail += " | " + rewardSummary.toString().substring(0, 450) + "...";
                    } else if (rewardSummary.length() > 0) {
                        rewardDetail += " | " + rewardSummary.toString();
                    }
                    
                    risingStonesAccountService.updateAccountRewardStatus(
                        accountId, rewardStatus, rewardDetail, allRawResponses.toString());
                } else {
                    logger.info("账号 {} 没有可领取的签到奖励", accountId);
                    risingStonesAccountService.updateAccountRewardStatus(
                        accountId, "NO_REWARD", "奖励列表为空 (共 0 个奖励)", allRawResponses.toString());
                }
            } else {
                String errorMsg = getApiMessage(rewardListResult);
                logger.error("账号 {} 获取签到奖励列表失败: {}", accountId, errorMsg);
                String failDetail = "获取奖励列表失败: " + errorMsg + " (code: " + (rewardListResult != null ? rewardListResult.getInteger("code") : "null") + ")";
                risingStonesAccountService.updateAccountRewardStatus(
                    accountId, "FAILED", failDetail, allRawResponses.toString());
            }
        } catch (IOException e) {
            logger.error("账号 {} 获取签到奖励列表时发生异常", accountId, e);
            risingStonesAccountService.updateAccountRewardStatus(
                accountId, "ERROR", "获取奖励列表异常: " + e.getMessage(), null);
        } catch (Exception e) {
            logger.error("账号 {} 领取奖励过程中发生未知异常", accountId, e);
            risingStonesAccountService.updateAccountRewardStatus(
                accountId, "ERROR", "领取奖励异常: " + e.getMessage(), null);
        }
    }

    /**
     * 发送通知消息
     */
    private void sendNotification(String title, String content) {
        String qq = getAdminQQ();
        if (qq == null || qq.isEmpty()) {
            logger.warn("未配置管理员QQ，跳过通知发送");
            return;
        }

        try {
            String message = String.format("%s\n\n%s\n\n执行时间: %s", 
                title, content, LocalDateTime.now().format(DATE_FORMATTER));
            
            napCatQQUtil.sendPrivateMessage(qq, message);
            logger.info("已发送通知消息: {}", title);
        } catch (IOException e) {
            logger.error("发送通知消息失败", e);
        }
    }

    /**
     * 手动触发签到任务（用于测试）
     */
    public void manualSignIn() {
        logger.info("手动触发每日签到任务");
        dailySignInTask();
    }

    /**
     * 手动触发单个账号签到任务（用于测试）
     */
    public void manualSignIn(String accountId) {
        logger.info("手动触发账号 {} 的签到任务", accountId);
        
        try {
            RisingStonesAccount account = risingStonesAccountService.getAccount(accountId);
            if (account == null) {
                logger.error("账号 {} 不存在", accountId);
                return;
            }

            if (!account.getEnabled()) {
                logger.warn("账号 {} 已被禁用", accountId);
                return;
            }

            JSONObject signInResult = risingStonesUtils.doSignIn(account.getCookies());
            String rawResponse = signInResult != null ? signInResult.toJSONString() : "null";
            
            if (signInResult != null && signInResult.getInteger("code") == 10001) {
                String message = getApiMessage(signInResult);
                logger.info("账号 {} 签到成功 - {}", accountId, message);
                risingStonesAccountService.updateAccountSignInStatus(
                    accountId, "SUCCESS", message, rawResponse);
                
                claimAvailableRewards(account.getCookies(), accountId);
                
                sendNotification("✅ 手动签到成功", 
                    "账号: " + account.getNickname() + "\n签到结果: " + message);
            } else {
                String errorMsg = getApiMessage(signInResult);
                logger.error("账号 {} 签到失败: {}", accountId, errorMsg);
                String statusDetail = "签到失败: " + errorMsg + " (code: " + (signInResult != null ? signInResult.getInteger("code") : "null") + ")";
                risingStonesAccountService.updateAccountSignInStatus(
                    accountId, "FAILED", statusDetail, rawResponse);
                sendNotification("❌ 手动签到失败", 
                    "账号: " + account.getNickname() + "\n签到失败: " + errorMsg);
            }
        } catch (IOException e) {
            logger.error("账号 {} 签到异常", accountId, e);
            risingStonesAccountService.updateAccountSignInStatus(
                accountId, "ERROR", "签到异常: " + e.getMessage(), null);
            sendNotification("❌ 手动签到异常", 
                "账号: " + accountId + "\n异常信息: " + e.getMessage());
        } catch (Exception e) {
            logger.error("手动签到任务执行发生未知异常", e);
            sendNotification("❌ 手动签到任务执行发生未知异常", 
                "异常信息: " + e.getMessage());
        }
    }

    /**
     * 手动触发奖励领取任务（用于测试）
     */
    public void manualClaimRewards() {
        logger.info("手动触发奖励领取任务");
        
        try {
            List<RisingStonesAccount> enabledAccounts = risingStonesAccountService.getEnabledAccounts();
            
            for (RisingStonesAccount account : enabledAccounts) {
                claimAvailableRewards(account.getCookies(), account.getAccountId());
            }
        } catch (Exception e) {
            logger.error("手动领取奖励任务执行发生未知异常", e);
            sendNotification("❌ 手动领取奖励任务执行发生未知异常", 
                "异常信息: " + e.getMessage());
        }
    }

    /**
     * 从API响应中获取消息，优先使用msg字段，其次使用message字段
     */
    private String getApiMessage(JSONObject response) {
        if (response == null) {
            return "未知错误";
        }
        String msg = response.getString("msg");
        if (msg != null && !msg.isEmpty()) {
            return msg;
        }
        String message = response.getString("message");
        if (message != null && !message.isEmpty()) {
            return message;
        }
        Integer code = response.getInteger("code");
        if (code != null) {
            return "错误码: " + code;
        }
        return "未知错误";
    }
}
