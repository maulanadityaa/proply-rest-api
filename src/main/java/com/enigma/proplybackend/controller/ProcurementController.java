package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.CommonResponseWithPage;
import com.enigma.proplybackend.model.response.PagingResponse;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import com.enigma.proplybackend.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.PROCUREMENT)
@RequiredArgsConstructor
public class ProcurementController {
    private final ProcurementService procurementService;

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<?> createNewProcurement(@RequestBody ProcurementRequest procurementRequest) {
        ProcurementResponse procurementResponse = procurementService.addProcurement(procurementRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Procurement created successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping(AppPath.APPROVE_PROCUREMENT)
    public ResponseEntity<?> approveProcurement(@RequestBody ProcurementDetailRequest procurementDetailRequest, @RequestHeader("Authorization") String authorization) {
        ProcurementResponse procurementResponse = procurementService.approveProcurement(procurementDetailRequest, authorization);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement approved successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping(AppPath.REJECT_PROCUREMENT)
    public ResponseEntity<?> rejectProcurement(@RequestBody ProcurementDetailRequest procurementDetailRequest, @RequestHeader("Authorization") String authorization) {
        ProcurementResponse procurementResponse = procurementService.rejectProcurement(procurementDetailRequest, authorization);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement rejected successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping(AppPath.CANCEL_PROCUREMENT)
    public ResponseEntity<?> cancelProcurement(@RequestBody ProcurementDetailRequest procurementDetailRequest) {
        ProcurementResponse procurementResponse = procurementService.cancelProcurement(procurementDetailRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement cancelled successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllProcurements() {
        List<ProcurementResponse> procurementResponses = procurementService.getAllProcurements();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ProcurementResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurements retrieved successfully")
                        .data(procurementResponses)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping(AppPath.GET_WITH_PAGE)
    public ResponseEntity<?> getAllByNameOrCategory(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        Page<ProcurementResponse> procurementResponsePage = procurementService.getAllByNameOrCategory(name, category, page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseWithPage.<List<ProcurementResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurements retrieved successfully")
                        .data(procurementResponsePage.getContent())
                        .paging(PagingResponse.builder()
                                .currentPage(page)
                                .size(size)
                                .totalPages(procurementResponsePage.getTotalPages())
                                .build())
                        .build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    @GetMapping(AppPath.GET_BY_USER_ID)
    public ResponseEntity<?> getProcurementsByUserId(@RequestParam(name = "user-id", required = false) String userId) {
        List<ProcurementResponse> procurementResponses = procurementService.getAllProcurementsByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ProcurementResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurements retrieved successfully")
                        .data(procurementResponses)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getProcurementById(@PathVariable String id) {
        ProcurementResponse procurementResponses = procurementService.getProcurementById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurements retrieved successfully")
                        .data(procurementResponses)
                        .build()
                );
    }
}
