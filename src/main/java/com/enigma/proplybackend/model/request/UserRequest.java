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
public class UserRequest {
    private String id;
    private String fullName;
    private String email;
    private Long birthDate;
    private EGender gender;
    private EMaritalStatus maritalStatus;
    private String divisionId;
}
