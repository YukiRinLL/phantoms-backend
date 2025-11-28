package com.phantoms.phantomsbackend.common.scheduler;

import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesLoginTool;
import com.phantoms.phantomsbackend.service.DaoYuKeyCacheService;
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
public class DaoYuKeyMonitorScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DaoYuKeyMonitorScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RisingStonesLoginTool risingStonesLoginTool;

    @Autowired
    private DaoYuKeyCacheService daoYuKeyCacheService;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Value("${monitor.notification.email:}")
    private String notificationEmail;

    @Value("${napcat.admin-qq:944989026}")
    private String notificationQQ;

    @Value("${monitor.check.enabled:true}")
    private boolean monitorEnabled;

    @Value("${monitor.notify.on-success:true}")
    private boolean notifyOnSuccess;

    // 连续失败次数
    private int consecutiveFailures = 0;
    // 最大允许连续失败次数
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    // 是否已经发送过警告
    private boolean warningSent = false;
    // 上次检查状态
    private boolean lastCheckSuccess = true;

    /**
     * 每30分钟检查一次DaoYu Key有效性
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30分钟
    public void checkDaoYuKeyValidity() {
        if (!monitorEnabled) {
            logger.info("DaoYu Key监控已禁用");
            return;
        }

        logger.info("开始检查DaoYu Key有效性 - {}", LocalDateTime.now().format(formatter));

        try {
            // 尝试使用当前DaoYu Key进行登录流程
            String[] loginResult = risingStonesLoginTool.getDaoYuTokenAndCookie();

            if (loginResult[0] != null && loginResult[1] != null) {
                // 登录成功
                handleSuccess(loginResult[0]);
                logger.info("DaoYu Key有效性检查通过 - Token: {}", loginResult[0].substring(0, Math.min(20, loginResult[0].length())) + "...");
            } else {
                // 登录失败
                handleFailure("DaoYu Key登录失败：返回的token或cookie为空");
            }

        } catch (Exception e) {
            handleFailure("DaoYu Key有效性检查异常: " + e.getMessage());
            logger.error("DaoYu Key检查过程中发生异常", e);
        }
    }

    /**
     * 每小时强制刷新一次缓存并检查
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1小时
    public void refreshAndCheckDaoYuKey() {
        if (!monitorEnabled) {
            return;
        }

        logger.info("执行强制刷新并检查DaoYu Key - {}", LocalDateTime.now().format(formatter));

        try {
            // 强制刷新缓存
            risingStonesLoginTool.refreshDaoYuKeyCache();

            // 等待一段时间让缓存刷新完成
            Thread.sleep(5000);

            // 重新检查有效性
            checkDaoYuKeyValidity();

        } catch (Exception e) {
            logger.error("强制刷新DaoYu Key过程中发生异常", e);
        }
    }

    private void handleSuccess(String token) {
        boolean wasFailure = consecutiveFailures > 0;
        consecutiveFailures = 0;

        // 状态从失败变为成功，发送恢复通知
        if (wasFailure && warningSent) {
            sendRecoveryNotification();
            warningSent = false;
        }

        // 每次成功都发送通知（如果配置了）
        if (notifyOnSuccess) {
            sendSuccessNotification(token, wasFailure);
        }

        lastCheckSuccess = true;
    }

    private void handleFailure(String errorMessage) {
        consecutiveFailures++;
        logger.warn("DaoYu Key检查失败，连续失败次数: {}", consecutiveFailures);

        boolean previousSuccess = lastCheckSuccess;
        lastCheckSuccess = false;

        // 达到最大失败次数时发送警告
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES && !warningSent) {
            sendWarningNotification(errorMessage, previousSuccess);
            warningSent = true;
        }
    }

    private void sendSuccessNotification(String token, boolean wasRecovery) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = wasRecovery ? "【恢复通知】DaoYu Key已恢复正常" : "【正常通知】DaoYu Key检查通过";

        String content = String.format(
                "DaoYu Key有效性检查通过！\n\n" +
                        "检查时间: %s\n" +
                        "Token状态: 有效\n" +
                        "Token前缀: %s\n" +
                        "系统状态: %s\n\n" +
                        "所有相关功能正常运行中。",
                currentTime,
                token.substring(0, Math.min(20, token.length())) + "...",
                wasRecovery ? "已从异常状态恢复" : "持续正常"
        );

        // 发送邮件通知
        sendEmailNotification(subject, content, "success");

        // 发送QQ消息通知
        sendQQNotification("✅ " + subject + "\n" + content);

        logger.info("已发送DaoYu Key检查通过通知");
    }

    private void sendWarningNotification(String errorMessage, boolean previousSuccess) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【紧急警告】DaoYu Key已失效";
        String content = String.format(
                "DaoYu Key有效性检查失败！\n\n" +
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

        logger.warn("已发送DaoYu Key失效警告通知");
    }

    private void sendRecoveryNotification() {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【恢复通知】DaoYu Key已恢复正常";
        String content = String.format(
                "DaoYu Key已从异常状态恢复正常！\n\n" +
                        "恢复时间: %s\n" +
                        "最大连续失败次数: %d\n" +
                        "系统功能现已恢复正常运行。",
                currentTime, consecutiveFailures
        );

        // 发送邮件通知
        sendEmailNotification(subject, content, "recovery");

        // 发送QQ消息通知
        sendQQNotification("✅ " + subject + "\n" + content);

        logger.info("已发送DaoYu Key恢复通知");
    }

    private void sendEmailNotification(String subject, String content, String type) {
        if (notificationEmail != null && !notificationEmail.isEmpty()) {
            try {
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("title", subject);
                templateVariables.put("content", content.replace("\n", "<br>"));
                templateVariables.put("timestamp", LocalDateTime.now().format(formatter));
                templateVariables.put("type", type); // success, warning, recovery

                emailUtil.sendDefaultHtmlEmail(notificationEmail, subject, templateVariables);
                logger.info("DaoYu Key {}邮件已发送至: {}", type, notificationEmail);
            } catch (Exception e) {
                logger.error("发送DaoYu Key {}邮件失败", type, e);
            }
        } else {
            logger.warn("未配置通知邮箱，跳过邮件发送");
        }
    }

    private void sendQQNotification(String message) {
        if (notificationQQ != null && !notificationQQ.isEmpty()) {
            try {
                napCatQQUtil.sendPrivateMessage(notificationQQ, message);
                logger.info("DaoYu Key通知QQ消息已发送至: {}", notificationQQ);
            } catch (IOException e) {
                logger.error("发送DaoYu Key通知QQ消息失败", e);
            }
        } else {
            logger.warn("未配置通知QQ，跳过QQ消息发送");
        }
    }

    /**
     * 手动触发检查（可用于测试）
     */
    public void manualCheck() {
        logger.info("手动触发DaoYu Key检查");
        checkDaoYuKeyValidity();
    }

    /**
     * 获取当前监控状态
     */
    public Map<String, Object> getMonitorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("monitorEnabled", monitorEnabled);
        status.put("consecutiveFailures", consecutiveFailures);
        status.put("warningSent", warningSent);
        status.put("lastCheckSuccess", lastCheckSuccess);
        status.put("notifyOnSuccess", notifyOnSuccess);
        status.put("lastCheckTime", LocalDateTime.now().format(formatter));
        status.put("notificationEmail", notificationEmail);
        status.put("notificationQQ", notificationQQ);
        return status;
    }

    /**
     * 设置是否在成功时发送通知
     */
    public void setNotifyOnSuccess(boolean notifyOnSuccess) {
        this.notifyOnSuccess = notifyOnSuccess;
    }
}