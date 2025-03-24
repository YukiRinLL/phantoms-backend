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

import java.util.Map;

@Component
public class EmailUtil {

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
        System.out.println("Simple email sent successfully");
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
            System.out.println("HTML email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

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
            System.out.println("HTML email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

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
            System.out.println("HTML email sent successfully");
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