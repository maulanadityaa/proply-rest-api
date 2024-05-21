package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ProcurementCategoryRequest;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;

import java.util.List;

public interface ProcurementCategoryService {
    ProcurementCategoryResponse addProcurementCategory(ProcurementCategoryRequest procurementCategoryRequest);

    ProcurementCategoryResponse updateProcurementCategory(ProcurementCategoryRequest procurementCategoryRequest);

    void deleteProcurementCategory(String procurementCategoryId);

    ProcurementCategoryResponse getProcurementCategoryById(String procurementCategoryId);

    List<ProcurementCategoryResponse> getAllProcurementCategories();
}
