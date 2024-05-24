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
public class ProcurementDetailResponse {
    private String procurementDetailId;
    private ItemResponse itemResponse;
    private EProcurementStatus status;
    private Integer quantity;
    private String notes;
    private UserResponse approvedBy;
    private Long approvedAt;
}
