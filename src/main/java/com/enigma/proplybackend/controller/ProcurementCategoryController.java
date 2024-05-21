package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.ProcurementCategoryRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;
import com.enigma.proplybackend.service.ProcurementCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.PROCUREMENT_CATEGORY)
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
public class ProcurementCategoryController {
    private final ProcurementCategoryService procurementCategoryService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProcurementCategory(@RequestBody ProcurementCategoryRequest procurementCategoryRequest) {
        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.addProcurementCategory(procurementCategoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ProcurementCategoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Procurement category added successfully")
                        .data(procurementCategoryResponse)
                        .build()
                );
    }

    @PutMapping
    public ResponseEntity<?> updateProcurementCategory(@RequestBody ProcurementCategoryRequest procurementCategoryRequest) {
        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.updateProcurementCategory(procurementCategoryRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementCategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement category updated successfully")
                        .data(procurementCategoryResponse)
                        .build()
                );
    }

    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getProcurementCategoryById(@PathVariable String id) {
        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementCategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement category retrieved successfully")
                        .data(procurementCategoryResponse)
                        .build()
                );
    }

    @GetMapping
    public ResponseEntity<?> getAllProcurementCategories() {
        List<ProcurementCategoryResponse> procurementCategoryResponses = procurementCategoryService.getAllProcurementCategories();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ProcurementCategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement categories retrieved successfully")
                        .data(procurementCategoryResponses)
                        .build()
                );
    }

    @DeleteMapping(AppPath.DELETE_BY_ID)
    public ResponseEntity<?> deleteProcurementCategoryById(@PathVariable String id) {
        procurementCategoryService.deleteProcurementCategory(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement category deleted successfully")
                        .build());
    }
}
