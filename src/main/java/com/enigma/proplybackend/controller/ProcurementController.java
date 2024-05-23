package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import com.enigma.proplybackend.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.PROCUREMENT)
@RequiredArgsConstructor
public class ProcurementController {
    private final ProcurementService procurementService;

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

    @PutMapping(AppPath.APPROVE_PROCUREMENT)
    public ResponseEntity<?> approveProcurement(@RequestBody ProcurementDetailRequest procurementDetailRequest) {
        ProcurementResponse procurementResponse = procurementService.approveProcurement(procurementDetailRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement approved successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

    @PutMapping(AppPath.REJECT_PROCUREMENT)
    public ResponseEntity<?> rejectProcurement(@RequestBody ProcurementDetailRequest procurementDetailRequest) {
        ProcurementResponse procurementResponse = procurementService.rejectProcurement(procurementDetailRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ProcurementResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Procurement rejected successfully")
                        .data(procurementResponse)
                        .build()
                );
    }

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
