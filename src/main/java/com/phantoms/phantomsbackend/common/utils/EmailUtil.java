package com.phantoms.phantomsbackend.common.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class EmailUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${spring.mail.from.name}")
    private String fromName;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromName + " <" + fromAddress + ">");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        logger.info("Simple email sent successfully");
    }

    /**
     * 发送 HTML 邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    邮件内容（HTML 格式）
     */
    public void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromName + " <" + fromAddress + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true 表示内容为 HTML
            mailSender.send(message);
            logger.info("HTML email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用默认邮件模板发送邮件
     */
    public void sendDefaultHtmlEmail(String to, String subject, Map<String, Object> templateVariables) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromName + " <" + fromAddress + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = generateHtmlContent("email/defaultEmailTemplate", templateVariables);
            helper.setText(htmlContent, true); // true 表示内容为 HTML
            mailSender.send(message);
            logger.info("HTML email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送欢迎邮件
     */
    public void sendWelcomeHtmlEmail(String to, String subject, Map<String, Object> templateVariables) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromName + " <" + fromAddress + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = generateHtmlContent("email/welcomeEmailTemplate", templateVariables);
            helper.setText(htmlContent, true); // true 表示内容为 HTML
            mailSender.send(message);
            logger.info("HTML email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送 DaoYu Key 监控通知邮件
     */
    public void sendDaoYuKeyNotificationEmail(String to, String subject, String recipientName,
                                              String messageBody, String footerText,
                                              String buttonLink, String buttonText,
                                              String footerCopyright) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromName + " <" + fromAddress + ">");
            helper.setTo(to);
            helper.setSubject(subject);

            // 准备模板变量
            Context context = new Context();
            context.setVariable("subject", subject);
            context.setVariable("recipientName", recipientName);
            context.setVariable("messageBody", messageBody);
            context.setVariable("footerText", footerText);
            context.setVariable("buttonLink", buttonLink);
            context.setVariable("buttonText", buttonText);
            context.setVariable("footerCopyright", footerCopyright);

            String htmlContent = templateEngine.process("email/defaultEmailTemplate", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("DaoYu Key notification email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送 DaoYu Key 状态通知邮件（简化版）
     */
    public void sendDaoYuKeyStatusEmail(String to, String subject, String status, String message, String timestamp) {
        MimeMessage messageObj = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(messageObj, true);
            helper.setFrom(fromName + " <" + fromAddress + ">");
            helper.setTo(to);
            helper.setSubject(subject);

            // 根据状态设置不同的样式和内容
            String recipientName = "尊敬的系统管理员：";
            String messageBody = message;
            String footerText = "请及时处理系统状态问题。";
            String buttonLink = "#";
            String buttonText = "查看详情";
            String footerCopyright = "版权所有 © 2025 Phantoms系统监控平台";

            // 根据状态调整按钮颜色和文本
            if (status.contains("成功") || status.contains("正常") || status.contains("恢复")) {
                buttonText = "✅ 系统正常";
            } else if (status.contains("警告") || status.contains("失败") || status.contains("异常")) {
                buttonText = "🚨 立即处理";
            }

            // 准备模板变量
            Context context = new Context();
            context.setVariable("subject", subject);
            context.setVariable("recipientName", recipientName);
            context.setVariable("messageBody", messageBody);
            context.setVariable("footerText", footerText);
            context.setVariable("buttonLink", buttonLink);
            context.setVariable("buttonText", buttonText);
            context.setVariable("footerCopyright", footerCopyright);

            String htmlContent = templateEngine.process("email/defaultEmailTemplate", context);
            helper.setText(htmlContent, true);
            mailSender.send(messageObj);
            logger.info("DaoYu Key status email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 HTML 内容
     *
     * @param templateName 模板名称
     * @param templateVariables 模板变量
     * @return HTML 内容
     */
    private String generateHtmlContent(String templateName, Map<String, Object> templateVariables) {
        Context context = new Context();
        context.setVariables(templateVariables);
        return templateEngine.process(templateName, context);
    }
}