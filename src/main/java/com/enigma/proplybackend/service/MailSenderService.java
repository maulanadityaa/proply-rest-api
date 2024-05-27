package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.MailRequest;
import org.thymeleaf.context.Context;

public interface MailSenderService {
    Boolean sendEmail(MailRequest mailRequest);

    Boolean sendEmailWithTemplate(MailRequest mailRequest, Context context);
}
