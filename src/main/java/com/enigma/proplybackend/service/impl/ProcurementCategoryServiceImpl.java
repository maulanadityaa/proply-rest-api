package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.ProcurementCategory;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ProcurementCategoryRequest;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;
import com.enigma.proplybackend.repository.ProcurementCategoryRepository;
import com.enigma.proplybackend.service.ProcurementCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcurementCategoryServiceImpl implements ProcurementCategoryService {
    private final ProcurementCategoryRepository procurementCategoryRepository;

    @Override
    public ProcurementCategoryResponse addProcurementCategory(ProcurementCategoryRequest procurementCategoryRequest) {
        try {
            ProcurementCategory procurementCategory = ProcurementCategory.builder()
                    .name(procurementCategoryRequest.getName())
                    .isActive(true)
                    .build();
            procurementCategoryRepository.save(procurementCategory);

            return toProcurementCategoryResponse(procurementCategory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Cannot create procurement category", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ProcurementCategoryResponse updateProcurementCategory(ProcurementCategoryRequest procurementCategoryRequest) {
        ProcurementCategory procurementCategory = procurementCategoryRepository.findById(procurementCategoryRequest.getProcurementCategoryId()).orElseThrow(() -> new ApplicationException("Procurement category not found", "Procurement category with id=" + procurementCategoryRequest.getProcurementCategoryId() + " not found", HttpStatus.NOT_FOUND));

        procurementCategory.setName(procurementCategoryRequest.getName());
        procurementCategoryRepository.save(procurementCategory);

        return toProcurementCategoryResponse(procurementCategory);
    }

    @Override
    public void deleteProcurementCategory(String procurementCategoryId) {
        ProcurementCategory procurementCategory = procurementCategoryRepository.findById(procurementCategoryId).orElseThrow(() -> new ApplicationException("Procurement category not found", "Procurement category with id=" + procurementCategoryId + " not found", HttpStatus.NOT_FOUND));

        procurementCategory.setIsActive(false);
        procurementCategoryRepository.save(procurementCategory);
    }

    @Override
    public ProcurementCategoryResponse getProcurementCategoryById(String procurementCategoryId) {
        ProcurementCategory procurementCategory = procurementCategoryRepository.findById(procurementCategoryId).orElseThrow(() -> new ApplicationException("Procurement category not found", "Procurement category with id=" + procurementCategoryId + " not found", HttpStatus.NOT_FOUND));

        return toProcurementCategoryResponse(procurementCategory);
    }

    @Override
    public List<ProcurementCategoryResponse> getAllProcurementCategories() {
        List<ProcurementCategory> procurementCategories = procurementCategoryRepository.findAll();
        List<ProcurementCategoryResponse> procurementCategoryResponseList = new ArrayList<>();

        if (procurementCategories.isEmpty())
            throw new ApplicationException("No procurement categories found", null, HttpStatus.NOT_FOUND);

        for (ProcurementCategory procurementCategory : procurementCategories) {
            procurementCategoryResponseList.add(toProcurementCategoryResponse(procurementCategory));
        }

        return procurementCategoryResponseList;
    }

    private static ProcurementCategoryResponse toProcurementCategoryResponse(ProcurementCategory procurementCategory) {
        return ProcurementCategoryResponse.builder()
                .procurementCategoryId(procurementCategory.getId())
                .name(procurementCategory.getName())
                .isActive(procurementCategory.getIsActive())
                .build();
    }
}
