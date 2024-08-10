package com.enigma.proplybackend.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String to;
    private String subject;
    private String body;
}
