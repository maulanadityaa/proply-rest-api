package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.entity.ProcurementDetail;

public interface ProcurementDetailService {
    ProcurementDetail addProcurementDetail(ProcurementDetail procurementDetail);

    ProcurementDetail getProcurementDetailById(String procurementDetailId);

}
