package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DailySignInScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailySignInScheduler.class);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RisingStonesUtils risingStonesUtils;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Value("${napcat.admin-qq:944989026}")
    private String adminQQ;

    @Value("${scheduler.signin.enabled:true}")
    private boolean signInEnabled;

    /**
     * 每日签到任务 - UTC+8每天00:05执行
     * 先执行签到，然后尝试领取可用奖励
     */
    @Scheduled(cron = "0 5 0 * * ?") // 每天00:05执行
//    @Scheduled(fixedRate = 60000)
    public void dailySignInTask() {
        if (!signInEnabled) {
            logger.info("每日签到任务已禁用");
            return;
        }

        logger.info("开始执行每日签到任务 - {}", LocalDateTime.now().format(DATE_FORMATTER));
        
        try {
            // 1. 执行每日签到
            JSONObject signInResult = risingStonesUtils.doSignIn();
            
            if (signInResult != null && signInResult.getInteger("code") == 10001) {
                logger.info("签到成功 - {}", signInResult.getString("message"));
                
                // 2. 尝试领取可用奖励
                claimAvailableRewards();
                
                // 发送成功通知
                sendNotification("✅ 每日签到任务执行成功", 
                    "签到结果: " + signInResult.getString("message"));
            } else {
                String errorMsg = signInResult != null ? signInResult.getString("message") : "未知错误";
                logger.error("签到失败: {}", errorMsg);
                sendNotification("❌ 每日签到任务执行失败", 
                    "签到失败: " + errorMsg);
            }
        } catch (IOException e) {
            logger.error("每日签到任务执行异常", e);
            sendNotification("❌ 每日签到任务执行异常", 
                "异常信息: " + e.getMessage());
        } catch (Exception e) {
            logger.error("每日签到任务执行发生未知异常", e);
            sendNotification("❌ 每日签到任务执行发生未知异常", 
                "异常信息: " + e.getMessage());
        }
    }

    /**
     * 领取可用的签到奖励
     */
    private void claimAvailableRewards() {
        logger.info("开始尝试领取签到奖励");
        
        try {
            // 获取当前月份
            String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
            
            // 获取签到奖励列表
            JSONObject rewardListResult = risingStonesUtils.getSignInRewardList(currentMonth);
            
            if (rewardListResult != null && rewardListResult.getInteger("code") == 10001) {
                JSONArray rewardList = rewardListResult.getJSONObject("data").getJSONArray("list");
                
                if (rewardList != null && !rewardList.isEmpty()) {
                    logger.info("获取到 {} 个签到奖励", rewardList.size());
                    
                    // 遍历奖励列表，尝试领取可用奖励
                    for (int i = 0; i < rewardList.size(); i++) {
                        JSONObject reward = rewardList.getJSONObject(i);
                        
                        // 检查奖励是否可领取
                        // 状态说明: 0-未达成, 1-可领取, 2-已领取
                        Integer status = reward.getInteger("status");
                        if (status != null && status == 1) {
                            // 领取奖励
                            Integer rewardId = reward.getInteger("id");
                            String rewardName = reward.getString("name");
                            
                            try {
                                JSONObject claimResult = risingStonesUtils.getSignInReward(rewardId, currentMonth);
                                
                                if (claimResult != null && claimResult.getInteger("code") == 10001) {
                                    logger.info("成功领取奖励: {} (ID: {})", rewardName, rewardId);
                                    sendNotification("✅ 领取签到奖励成功", 
                                        "奖励名称: " + rewardName + "\n奖励ID: " + rewardId);
                                } else {
                                    String errorMsg = claimResult != null ? claimResult.getString("message") : "未知错误";
                                    logger.error("领取奖励失败: {} (ID: {}), 错误信息: {}", rewardName, rewardId, errorMsg);
                                }
                            } catch (IOException e) {
                                logger.error("领取奖励时发生异常: {} (ID: {})", rewardName, rewardId, e);
                            }
                        }
                    }
                }
            } else {
                String errorMsg = rewardListResult != null ? rewardListResult.getString("message") : "未知错误";
                logger.error("获取签到奖励列表失败: {}", errorMsg);
            }
        } catch (IOException e) {
            logger.error("获取签到奖励列表时发生异常", e);
        } catch (Exception e) {
            logger.error("领取奖励过程中发生未知异常", e);
        }
    }

    /**
     * 发送通知消息
     */
    private void sendNotification(String title, String content) {
        if (adminQQ == null || adminQQ.isEmpty()) {
            logger.warn("未配置管理员QQ，跳过通知发送");
            return;
        }

        try {
            String message = String.format("%s\n\n%s\n\n执行时间: %s", 
                title, content, LocalDateTime.now().format(DATE_FORMATTER));
            
            napCatQQUtil.sendPrivateMessage(adminQQ, message);
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
     * 手动触发奖励领取任务（用于测试）
     */
    public void manualClaimRewards() {
        logger.info("手动触发奖励领取任务");
        claimAvailableRewards();
    }
}
