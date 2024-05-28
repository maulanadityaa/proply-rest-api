package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ProcurementApprovalRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProcurementService {
    ProcurementResponse addProcurement(ProcurementRequest procurementRequest);

    ProcurementResponse getProcurementById(String procurementId);

    ProcurementResponse approveProcurement(ProcurementApprovalRequest procurementApprovalRequest, String authorization);

    ProcurementResponse rejectProcurement(ProcurementApprovalRequest procurementApprovalRequest, String authorization);

    ProcurementResponse cancelProcurement(ProcurementApprovalRequest procurementApprovalRequest);

    List<ProcurementResponse> getAllProcurements();

    Page<ProcurementResponse> getAllByNameOrCategory(String name, String category, Integer page, Integer size);

    List<ProcurementResponse> getAllProcurementsByUserId(String userId);
}
