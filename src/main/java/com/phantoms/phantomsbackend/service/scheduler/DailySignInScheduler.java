package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import com.phantoms.phantomsbackend.service.SystemConfigService.LoginAccount;
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

    private String getAdminQQ() {
        return systemConfigService.getString("napcat.admin-qq", "944989026");
    }

    private boolean isSignInEnabled() {
        return systemConfigService.getBoolean("scheduler.signin.enabled", true);
    }

    @Scheduled(cron = "0 5 0 * * ?")
//    @Scheduled(fixedRate = 60000)
    public void dailySignInTask() {
        if (!isSignInEnabled()) {
            logger.info("每日签到任务已禁用");
            return;
        }

        logger.info("开始执行每日签到任务 - {}", LocalDateTime.now().format(DATE_FORMATTER));

        List<LoginAccount> accounts = systemConfigService.getEnabledLoginAccounts();
        
        if (accounts.isEmpty()) {
            logger.warn("未找到任何启用的登录账号");
            sendNotification("❌ 每日签到任务执行失败", "未找到任何启用的登录账号");
            return;
        }

        logger.info("共找到 {} 个启用的登录账号", accounts.size());

        int successCount = 0;
        int failCount = 0;
        StringBuilder resultSummary = new StringBuilder();

        for (LoginAccount account : accounts) {
            logger.info("开始处理账号: {} ({})", account.getNickname(), account.getAccountId());
            try {
                JSONObject signInResult = risingStonesUtils.doSignInWithCookies(account.getCookies());

                if (signInResult != null && signInResult.getInteger("code") == 10001) {
                    successCount++;
                    String message = signInResult.getString("message");
                    logger.info("账号 {} 签到成功 - {}", account.getNickname(), message);
                    
                    systemConfigService.updateAccountSignInResult(account.getAccountId(), System.currentTimeMillis(), "成功: " + message);
                    
                    claimAvailableRewards(account);
                    
                    resultSummary.append("\n✅ ").append(account.getNickname()).append(": ").append(message);
                } else {
                    failCount++;
                    String errorMsg = signInResult != null ? signInResult.getString("message") : "未知错误";
                    logger.error("账号 {} 签到失败: {}", account.getNickname(), errorMsg);
                    
                    systemConfigService.updateAccountSignInResult(account.getAccountId(), System.currentTimeMillis(), "失败: " + errorMsg);
                    
                    resultSummary.append("\n❌ ").append(account.getNickname()).append(": ").append(errorMsg);
                }
            } catch (IOException e) {
                failCount++;
                logger.error("账号 {} 签到时发生异常", account.getNickname(), e);
                
                systemConfigService.updateAccountSignInResult(account.getAccountId(), System.currentTimeMillis(), "异常: " + e.getMessage());
                
                resultSummary.append("\n❌ ").append(account.getNickname()).append(": 异常 - ").append(e.getMessage());
            } catch (Exception e) {
                failCount++;
                logger.error("账号 {} 签到时发生未知异常", account.getNickname(), e);
                
                systemConfigService.updateAccountSignInResult(account.getAccountId(), System.currentTimeMillis(), "未知异常: " + e.getMessage());
                
                resultSummary.append("\n❌ ").append(account.getNickname()).append(": 未知异常 - ").append(e.getMessage());
            }
        }

        String title = failCount == 0 ? "✅ 每日签到任务执行成功" : (successCount > 0 ? "⚠️ 每日签到任务部分完成" : "❌ 每日签到任务全部失败");
        String content = String.format("成功: %d / 失败: %d\n\n各账号签到结果:%s", 
                successCount, failCount, resultSummary.toString());
        
        sendNotification(title, content);
    }

    private void claimAvailableRewards(LoginAccount account) {
        logger.info("开始尝试为账号 {} 领取签到奖励", account.getNickname());
        
        try {
            String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
            
            JSONObject rewardListResult = risingStonesUtils.getSignInRewardListWithCookies(account.getCookies(), currentMonth);
            
            if (rewardListResult != null && rewardListResult.getInteger("code") == 10001) {
                JSONArray rewardList = rewardListResult.getJSONObject("data").getJSONArray("list");
                
                if (rewardList != null && !rewardList.isEmpty()) {
                    logger.info("账号 {} 获取到 {} 个签到奖励", account.getNickname(), rewardList.size());
                    
                    for (int i = 0; i < rewardList.size(); i++) {
                        JSONObject reward = rewardList.getJSONObject(i);
                        
                        Integer status = reward.getInteger("status");
                        if (status != null && status == 1) {
                            Integer rewardId = reward.getInteger("id");
                            String rewardName = reward.getString("name");
                            
                            try {
                                JSONObject claimResult = risingStonesUtils.getSignInRewardWithCookies(account.getCookies(), rewardId, currentMonth);
                                
                                if (claimResult != null && claimResult.getInteger("code") == 10001) {
                                    logger.info("账号 {} 成功领取奖励: {} (ID: {})", account.getNickname(), rewardName, rewardId);
                                    sendNotification("✅ 领取签到奖励成功", 
                                        "账号: " + account.getNickname() + "\n奖励名称: " + rewardName + "\n奖励ID: " + rewardId);
                                } else {
                                    String errorMsg = claimResult != null ? claimResult.getString("message") : "未知错误";
                                    logger.error("账号 {} 领取奖励失败: {} (ID: {}), 错误信息: {}", account.getNickname(), rewardName, rewardId, errorMsg);
                                }
                            } catch (IOException e) {
                                logger.error("账号 {} 领取奖励时发生异常: {} (ID: {})", account.getNickname(), rewardName, rewardId, e);
                            }
                        }
                    }
                }
            } else {
                String errorMsg = rewardListResult != null ? rewardListResult.getString("message") : "未知错误";
                logger.error("账号 {} 获取签到奖励列表失败: {}", account.getNickname(), errorMsg);
            }
        } catch (IOException e) {
            logger.error("账号 {} 获取签到奖励列表时发生异常", account.getNickname(), e);
        } catch (Exception e) {
            logger.error("账号 {} 领取奖励过程中发生未知异常", account.getNickname(), e);
        }
    }

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

    public void manualSignIn() {
        logger.info("手动触发每日签到任务");
        dailySignInTask();
    }

    public void manualClaimRewards() {
        logger.info("手动触发奖励领取任务");
        List<LoginAccount> accounts = systemConfigService.getEnabledLoginAccounts();
        for (LoginAccount account : accounts) {
            claimAvailableRewards(account);
        }
    }
}
