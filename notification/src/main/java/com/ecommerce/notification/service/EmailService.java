package com.ecommerce.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            log.info("HTML EMAIL SENT to {} using template {}", to, templateName);
        } catch (Exception e) {
            log.error("FAILED TO SEND EMAIL to {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendGeneralNotificationEmail(String to, Map<String, Object> notificationDetails) {
        sendEmail(to, "ShopVerse Notification", "general-notification", notificationDetails);
    }

    public void sendPasswordResetEmail(String to, Map<String, Object> resetDetails) {
        sendEmail(to, "Password Reset Request - ShopVerse", "password-reset", resetDetails);
    }
}
