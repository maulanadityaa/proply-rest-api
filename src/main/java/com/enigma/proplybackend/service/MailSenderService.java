package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.MailRequest;

public interface MailSenderService {
    Boolean sendEmail(MailRequest mailRequest);
}
