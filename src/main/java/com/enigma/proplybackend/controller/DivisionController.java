package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.DivisionRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.DivisionResponse;
import com.enigma.proplybackend.service.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping(AppPath.DIVISIONS)
@RequiredArgsConstructor
public class DivisionController {
    private final DivisionService divisionService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addDivision(@RequestBody DivisionRequest divisionRequest) {
        DivisionResponse divisionResponse = divisionService.addDivision(divisionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.<DivisionResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Division added successfully")
                .data(divisionResponse)
                .build());
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateDivision(@RequestBody DivisionRequest divisionRequest) {
        DivisionResponse divisionResponse = divisionService.updateDivision(divisionRequest);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<DivisionResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Division updated successfully")
                .data(divisionResponse)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> getAllDivision() {
        List<DivisionResponse> divisionResponses = divisionService.getAllDivisions();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<List<DivisionResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Divisions retrieved successfully")
                .data(divisionResponses)
                .build());
    }

    @GetMapping(AppPath.ACTIVE_STATUS)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> getAllActiveDivision() {
        List<DivisionResponse> divisionResponses = divisionService.getAllDivisionsWhereActive();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<List<DivisionResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Active divisions retrieved successfully")
                .data(divisionResponses)
                .build());
    }

    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getDivisionById(@PathVariable String id) {
        DivisionResponse divisionResponse = divisionService.getDivisionById(id);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<DivisionResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Division retrieved successfully")
                .data(divisionResponse)
                .build());
    }

    @DeleteMapping(AppPath.DELETE_BY_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteDivision(@PathVariable String id) {
        divisionService.deleteDivision(id);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.<DivisionResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Division deleted successfully")
                .build());
    }

}
