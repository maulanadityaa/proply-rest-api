package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.ProcurementDetail;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.repository.ProcurementDetailRepository;
import com.enigma.proplybackend.service.ProcurementDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcurementDetailServiceImpl implements ProcurementDetailService {
    private final ProcurementDetailRepository procurementDetailRepository;

    @Override
    public ProcurementDetail addProcurementDetail(ProcurementDetail procurementDetail) {
        return procurementDetailRepository.save(procurementDetail);
    }

    @Override
    public ProcurementDetail getProcurementDetailById(String procurementDetailId) {
        return procurementDetailRepository.findById(procurementDetailId).orElseThrow(() -> new ApplicationException("Procurement detail not found", "Procurement detail with id=" + procurementDetailId + " not found", HttpStatus.NOT_FOUND));
    }
}
