package com.enigma.proplybackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementRequest {
    private String id;
    private String userId;
    private String procurementCategoryId;
    private List<ProcurementDetailRequest> procurementDetailRequests;
}
