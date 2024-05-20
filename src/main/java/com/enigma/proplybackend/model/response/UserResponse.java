package com.enigma.proplybackend.model.response;

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
public class UserResponse {
    private String userId;
    private String fullName;
    private Long birthDate;
    private EGender gender;
    private EMaritalStatus maritalStatus;
    private UserCredentialResponse userCredentialResponse;
    private DivisionResponse divisionResponse;
    private Boolean isActive;
}
