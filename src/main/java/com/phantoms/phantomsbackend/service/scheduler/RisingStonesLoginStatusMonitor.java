package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesSigninHelper;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class RisingStonesLoginStatusMonitor {

    private static final Logger logger = LoggerFactory.getLogger(RisingStonesLoginStatusMonitor.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RisingStonesSigninHelper risingStonesSigninHelper;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    private String getNotificationEmail() {
        return systemConfigService.getString("monitor.notification.email", "");
    }

    private String getNotificationQQ() {
        return systemConfigService.getString("napcat.admin-qq", "944989026");
    }

    private boolean isMonitorEnabled() {
        return systemConfigService.getBoolean("monitor.check.enabled", true);
    }

    private boolean isNotifyOnSuccess() {
        return systemConfigService.getBoolean("monitor.notification.notify-on-success", false);
    }

    // 连续失败次数
    private int consecutiveFailures = 0;
    // 最大允许连续失败次数
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    // 是否已经发送过警告
    private boolean warningSent = false;
    // 上次检查状态
    private boolean lastCheckSuccess = true;

    /**
     * 每30分钟检查一次登录状态（cookies有效性）
     */
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000) // 2小时
    public void checkLoginStatus() {
        if (!isMonitorEnabled()) {
            logger.info("登录状态监控已禁用");
            return;
        }

        logger.info("开始检查登录状态 - {}", LocalDateTime.now().format(formatter));

        try {
            // 使用当前cookies检查登录状态
            JSONObject loginResult = risingStonesSigninHelper.checkLoginStatus();

            if (loginResult != null && loginResult.getInteger("code") == 10000) {
                // 登录状态正常
                handleSuccess();
                logger.info("登录状态检查通过 - 状态: 正常");
            } else {
                // 登录状态异常
                String errorMsg = loginResult != null ? 
                    loginResult.getString("message") : "未知错误";
                handleFailure("登录状态异常：" + errorMsg);
            }

        } catch (Exception e) {
            handleFailure("登录状态检查异常: " + e.getMessage());
            logger.error("登录状态检查过程中发生异常", e);
        }
    }

    private void handleSuccess() {
        boolean wasFailure = consecutiveFailures > 0;
        consecutiveFailures = 0;

        // 状态从失败变为成功，发送恢复通知
        if (wasFailure && warningSent) {
            sendRecoveryNotification();
            warningSent = false;
        }

        // 每次成功都发送通知（如果配置了）
        if (isNotifyOnSuccess()) {
            sendSuccessNotification(wasFailure);
        }

        lastCheckSuccess = true;
    }

    private void handleFailure(String errorMessage) {
        consecutiveFailures++;
        logger.warn("登录状态检查失败，连续失败次数: {}", consecutiveFailures);

        boolean previousSuccess = lastCheckSuccess;
        lastCheckSuccess = false;

        // 达到最大失败次数时发送警告
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES && !warningSent) {
            sendWarningNotification(errorMessage, previousSuccess);
            warningSent = true;
        }
    }

    private void sendSuccessNotification(boolean wasRecovery) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = wasRecovery ? "【恢复通知】登录状态已恢复正常" : "【正常通知】登录状态检查通过";

        String content = String.format(
                "登录状态检查通过！\n\n" +
                        "检查时间: %s\n" +
                        "状态: 有效\n" +
                        "系统状态: %s\n\n" +
                        "所有相关功能正常运行中。",
                currentTime,
                wasRecovery ? "已从异常状态恢复" : "持续正常"
        );

        // 发送邮件通知
        sendEmailNotification(subject, content, "success");

        // 发送QQ消息通知
        sendQQNotification("✅ " + subject + "\n" + content);

        logger.info("已发送登录状态检查通过通知");
    }

    private void sendWarningNotification(String errorMessage, boolean previousSuccess) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【紧急警告】登录状态异常";
        String content = String.format(
                "登录状态检查失败！\n\n" +
                        "失败时间: %s\n" +
                        "失败原因: %s\n" +
                        "连续失败次数: %d\n" +
                        "之前状态: %s\n\n" +
                        "请及时处理，否则可能导致相关功能无法正常使用！",
                currentTime, errorMessage, consecutiveFailures,
                previousSuccess ? "正常" : "已异常"
        );

        // 发送邮件通知
        sendEmailNotification(subject, content, "warning");

        // 发送QQ消息通知
        sendQQNotification("🚨 " + subject + "\n" + content);

        logger.warn("已发送登录状态异常警告通知");
    }

    private void sendRecoveryNotification() {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【恢复通知】登录状态已恢复正常";
        String content = String.format(
                "登录状态已从异常状态恢复正常！\n\n" +
                        "恢复时间: %s\n" +
                        "最大连续失败次数: %d\n" +
                        "系统功能现已恢复正常运行。",
                currentTime, consecutiveFailures
        );

        // 发送邮件通知
        sendEmailNotification(subject, content, "recovery");

        // 发送QQ消息通知
        sendQQNotification("✅ " + subject + "\n" + content);

        logger.info("已发送登录状态恢复通知");
    }

    private void sendEmailNotification(String subject, String content, String type) {
        String email = getNotificationEmail();
        if (email != null && !email.isEmpty()) {
            try {
                String recipientName = "尊敬的系统管理员：";
                String messageBody = content.replace("\n", "<br>");
                String footerText = getFooterTextByType(type);
                String buttonLink = "#";
                String buttonText = getButtonTextByType(type);
                String footerCopyright = "版权所有 © 2025 Phantoms系统监控平台";

                emailUtil.sendDaoYuKeyNotificationEmail(
                        email,
                        subject,
                        recipientName,
                        messageBody,
                        footerText,
                        buttonLink,
                        buttonText,
                        footerCopyright
                );
                logger.info("登录状态 {}邮件已发送至: {}", type, email);
            } catch (Exception e) {
                logger.error("发送登录状态 {}邮件失败", type, e);
            }
        } else {
            logger.warn("未配置通知邮箱，跳过邮件发送");
        }
    }

    private String getFooterTextByType(String type) {
        switch (type) {
            case "success":
                return "系统运行正常，无需操作。";
            case "warning":
                return "请及时处理系统异常，避免影响服务。";
            case "recovery":
                return "系统已恢复正常运行。";
            default:
                return "感谢您的关注。";
        }
    }

    private String getButtonTextByType(String type) {
        switch (type) {
            case "success":
                return "✅ 系统正常";
            case "warning":
                return "🚨 立即处理";
            case "recovery":
                return "🔄 已恢复";
            default:
                return "查看详情";
        }
    }

    private void sendQQNotification(String message) {
        String qq = getNotificationQQ();
        if (qq != null && !qq.isEmpty()) {
            try {
                napCatQQUtil.sendPrivateMessage(qq, message);
                logger.info("登录状态通知QQ消息已发送至: {}", qq);
            } catch (IOException e) {
                logger.error("发送登录状态通知QQ消息失败", e);
            }
        } else {
            logger.warn("未配置通知QQ，跳过QQ消息发送");
        }
    }

    /**
     * 手动触发检查（可用于测试）
     */
    public void manualCheck() {
        logger.info("手动触发登录状态检查");
        checkLoginStatus();
    }

    /**
     * 获取当前监控状态
     */
    public Map<String, Object> getMonitorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("monitorEnabled", isMonitorEnabled());
        status.put("consecutiveFailures", consecutiveFailures);
        status.put("warningSent", warningSent);
        status.put("lastCheckSuccess", lastCheckSuccess);
        status.put("notifyOnSuccess", isNotifyOnSuccess());
        status.put("lastCheckTime", LocalDateTime.now().format(formatter));
        status.put("notificationEmail", getNotificationEmail());
        status.put("notificationQQ", getNotificationQQ());
        return status;
    }
}