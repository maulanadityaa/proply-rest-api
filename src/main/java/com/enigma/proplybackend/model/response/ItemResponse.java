package com.enigma.proplybackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private String itemId;
    private String name;
    private ItemCategoryResponse itemCategoryResponse;
    private Long createdAt;
    private Long updatedAt;
    private Boolean isActive;
}
