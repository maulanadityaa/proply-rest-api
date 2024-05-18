package com.enigma.proplybackend.model.request;

import com.enigma.proplybackend.constant.EGender;
import com.enigma.proplybackend.constant.EMaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
    private String fullName;
    private Long birthDate;
    private EGender gender;
    private EMaritalStatus maritalStatus;
    private String divisionId;
}
