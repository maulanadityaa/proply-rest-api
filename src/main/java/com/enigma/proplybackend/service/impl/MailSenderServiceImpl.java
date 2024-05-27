package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.request.MailRequest;
import com.enigma.proplybackend.service.MailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public Boolean sendEmail(MailRequest mailRequest) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(mailRequest.getTo());
            simpleMailMessage.setSubject(mailRequest.getSubject());
            simpleMailMessage.setText(mailRequest.getBody());
            javaMailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean sendEmailWithTemplate(MailRequest mailRequest, Context context) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            String htmlContent = templateEngine.process("email-template", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            // Handle exception
            e.printStackTrace();
            return false;
        }
    }
}
