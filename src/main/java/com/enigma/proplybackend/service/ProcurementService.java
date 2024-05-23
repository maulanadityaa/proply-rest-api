package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ProcurementResponse;

import java.util.List;

public interface ProcurementService {
    ProcurementResponse addProcurement(ProcurementRequest procurementRequest);

    ProcurementResponse getProcurementById(String procurementId);

    ProcurementResponse approveProcurement(ProcurementDetailRequest procurementDetailRequest);

    ProcurementResponse rejectProcurement(ProcurementDetailRequest procurementDetailRequest);

    ProcurementResponse cancelProcurement(ProcurementDetailRequest procurementDetailRequest);

    List<ProcurementResponse> getAllProcurements();

    List<ProcurementResponse> getAllProcurementsByUserId(String userId);
}
