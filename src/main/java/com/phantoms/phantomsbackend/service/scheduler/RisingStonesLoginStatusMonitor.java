package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.EmailUtil;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RisingStonesSigninHelper;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import com.phantoms.phantomsbackend.service.SystemConfigService.LoginAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    private final Map<String, Integer> accountConsecutiveFailures = new ConcurrentHashMap<>();
    private final Map<String, Boolean> accountWarningSent = new ConcurrentHashMap<>();
    private final Map<String, Boolean> accountLastCheckSuccess = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000)
    public void checkLoginStatus() {
        if (!isMonitorEnabled()) {
            logger.info("登录状态监控已禁用");
            return;
        }

        logger.info("开始检查登录状态 - {}", LocalDateTime.now().format(formatter));

        List<LoginAccount> accounts = systemConfigService.getEnabledLoginAccounts();
        
        if (accounts.isEmpty()) {
            logger.warn("未找到任何启用的登录账号");
            return;
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder resultSummary = new StringBuilder();

        for (LoginAccount account : accounts) {
            logger.info("检查账号登录状态: {} ({})", account.getNickname(), account.getAccountId());
            
            try {
                JSONObject loginResult = risingStonesSigninHelper.checkLoginStatusWithCookies(account.getCookies());

                if (loginResult != null && loginResult.getInteger("code") == 10000) {
                    successCount++;
                    handleAccountSuccess(account.getAccountId(), account.getNickname());
                    resultSummary.append("\n✅ ").append(account.getNickname()).append(": 正常");
                } else {
                    failCount++;
                    String errorMsg = loginResult != null ? 
                        loginResult.getString("message") : "未知错误";
                    handleAccountFailure(account.getAccountId(), account.getNickname(), errorMsg);
                    resultSummary.append("\n❌ ").append(account.getNickname()).append(": ").append(errorMsg);
                }
            } catch (Exception e) {
                failCount++;
                handleAccountFailure(account.getAccountId(), account.getNickname(), "异常: " + e.getMessage());
                resultSummary.append("\n❌ ").append(account.getNickname()).append(": 异常 - ").append(e.getMessage());
                logger.error("账号 {} 登录状态检查过程中发生异常", account.getNickname(), e);
            }
        }

        logger.info("登录状态检查完成 - 成功: {}, 失败: {}", successCount, failCount);
        
        if (failCount > 0) {
            String subject = failCount == accounts.size() ? "【紧急警告】所有账号登录状态异常" : "【警告】部分账号登录状态异常";
            String content = String.format("登录状态检查结果：\n\n成功: %d / 失败: %d\n\n各账号状态:%s\n\n检查时间: %s",
                    successCount, failCount, resultSummary.toString(), LocalDateTime.now().format(formatter));
            sendWarningNotification(subject, content);
        } else if (isNotifyOnSuccess()) {
            String subject = "【正常通知】所有账号登录状态检查通过";
            String content = String.format("所有 %d 个账号登录状态检查通过！\n\n检查时间: %s", 
                    successCount, LocalDateTime.now().format(formatter));
            sendSuccessNotification(subject, content);
        }
    }

    private void handleAccountSuccess(String accountId, String nickname) {
        Integer failures = accountConsecutiveFailures.getOrDefault(accountId, 0);
        boolean wasFailure = failures > 0;
        accountConsecutiveFailures.put(accountId, 0);

        if (wasFailure && Boolean.TRUE.equals(accountWarningSent.get(accountId))) {
            sendRecoveryNotification(nickname);
            accountWarningSent.put(accountId, false);
        }

        if (isNotifyOnSuccess() && wasFailure) {
            sendSuccessNotification("【恢复通知】账号 " + nickname + " 登录状态已恢复", 
                "账号: " + nickname + "\n状态: 已恢复正常\n检查时间: " + LocalDateTime.now().format(formatter));
        }

        accountLastCheckSuccess.put(accountId, true);
        logger.info("账号 {} 登录状态检查通过", nickname);
    }

    private void handleAccountFailure(String accountId, String nickname, String errorMessage) {
        int failures = accountConsecutiveFailures.getOrDefault(accountId, 0) + 1;
        accountConsecutiveFailures.put(accountId, failures);
        
        boolean previousSuccess = accountLastCheckSuccess.getOrDefault(accountId, true);
        accountLastCheckSuccess.put(accountId, false);

        logger.warn("账号 {} 登录状态检查失败，连续失败次数: {}", nickname, failures);

        if (failures >= MAX_CONSECUTIVE_FAILURES && !Boolean.TRUE.equals(accountWarningSent.get(accountId))) {
            sendAccountWarningNotification(nickname, errorMessage, failures, previousSuccess);
            accountWarningSent.put(accountId, true);
        }
    }

    private void sendSuccessNotification(String subject, String content) {
        sendEmailNotification(subject, content, "success");
        sendQQNotification("✅ " + subject + "\n" + content);
        logger.info("已发送登录状态检查通过通知");
    }

    private void sendAccountWarningNotification(String nickname, String errorMessage, int failures, boolean previousSuccess) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【紧急警告】账号 " + nickname + " 登录状态异常";
        String content = String.format(
                "账号 [%s] 登录状态检查失败！\n\n" +
                        "失败时间: %s\n" +
                        "失败原因: %s\n" +
                        "连续失败次数: %d\n" +
                        "之前状态: %s\n\n" +
                        "请及时处理，否则可能导致该账号无法正常签到！",
                nickname, currentTime, errorMessage, failures,
                previousSuccess ? "正常" : "已异常"
        );

        sendEmailNotification(subject, content, "warning");
        sendQQNotification("🚨 " + subject + "\n" + content);
        logger.warn("已发送账号 {} 登录状态异常警告通知", nickname);
    }

    private void sendWarningNotification(String subject, String content) {
        sendEmailNotification(subject, content, "warning");
        sendQQNotification("🚨 " + subject + "\n" + content);
        logger.warn("已发送登录状态异常警告通知");
    }

    private void sendRecoveryNotification(String nickname) {
        String currentTime = LocalDateTime.now().format(formatter);
        String subject = "【恢复通知】账号 " + nickname + " 登录状态已恢复";
        String content = String.format(
                "账号 [%s] 登录状态已从异常状态恢复正常！\n\n" +
                        "恢复时间: %s\n" +
                        "系统功能现已恢复正常运行。",
                nickname, currentTime
        );

        sendEmailNotification(subject, content, "recovery");
        sendQQNotification("✅ " + subject + "\n" + content);
        logger.info("已发送账号 {} 登录状态恢复通知", nickname);
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

    public void manualCheck() {
        logger.info("手动触发登录状态检查");
        checkLoginStatus();
    }

    public Map<String, Object> getMonitorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("monitorEnabled", isMonitorEnabled());
        status.put("notifyOnSuccess", isNotifyOnSuccess());
        status.put("lastCheckTime", LocalDateTime.now().format(formatter));
        status.put("notificationEmail", getNotificationEmail());
        status.put("notificationQQ", getNotificationQQ());
        status.put("accountConsecutiveFailures", accountConsecutiveFailures);
        status.put("accountWarningSent", accountWarningSent);
        status.put("accountLastCheckSuccess", accountLastCheckSuccess);
        return status;
    }
}
