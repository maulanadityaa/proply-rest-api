package com.enigma.proplybackend.model.response;

import com.enigma.proplybackend.constant.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialResponse {
    private String userCredentialId;
    private String email;
    private ERole role;
}
