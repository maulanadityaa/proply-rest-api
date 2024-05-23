package com.enigma.proplybackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementResponse {
    private String procurementId;
    private UserResponse userResponse;
    private ProcurementCategoryResponse procurementCategoryResponse;
    private List<ProcurementDetailResponse> procurementDetailResponses;
    private Long createdAt;
    private Long updatedAt;
}
