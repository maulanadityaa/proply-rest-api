package com.enigma.proplybackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementCategoryResponse {
    private String procurementCategoryId;
    private String name;
    private Boolean isActive;
}
