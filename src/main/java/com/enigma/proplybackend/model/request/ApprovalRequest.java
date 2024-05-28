package com.enigma.proplybackend.model.request;

import com.enigma.proplybackend.constant.EProcurementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequest {
    private String approvalId;
    private String userId;
    private String notes;
    private EProcurementStatus status;
}
