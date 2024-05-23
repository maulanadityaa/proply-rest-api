package com.enigma.proplybackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementDetailRequest {
    private String id;
    private String procurementId;
    private String itemId;
    private Integer quantity;
}
