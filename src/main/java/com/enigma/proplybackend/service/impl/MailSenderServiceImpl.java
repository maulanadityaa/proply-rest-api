package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.request.MailRequest;
import com.enigma.proplybackend.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;

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
}
