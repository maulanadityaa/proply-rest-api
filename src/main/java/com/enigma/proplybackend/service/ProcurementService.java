package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProcurementService {
    ProcurementResponse addProcurement(ProcurementRequest procurementRequest);

    ProcurementResponse getProcurementById(String procurementId);

    ProcurementResponse approveProcurement(ProcurementDetailRequest procurementDetailRequest, String authorization);

    ProcurementResponse rejectProcurement(ProcurementDetailRequest procurementDetailRequest, String authorization);

    ProcurementResponse cancelProcurement(ProcurementDetailRequest procurementDetailRequest);

    List<ProcurementResponse> getAllProcurements();

    Page<ProcurementResponse> getAllByNameOrCategory(String name, String category, Integer page, Integer size);

    List<ProcurementResponse> getAllProcurementsByUserId(String userId);
}
