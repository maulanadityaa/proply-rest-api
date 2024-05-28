package com.enigma.proplybackend.model.response;

import com.enigma.proplybackend.constant.EProcurementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalResponse {
    private UserResponse userResponse;
    private EProcurementStatus status;
    private String notes;
}
