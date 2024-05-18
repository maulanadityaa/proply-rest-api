package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.DivisionRequest;
import com.enigma.proplybackend.model.response.DivisionResponse;

import java.util.List;

public interface DivisionService {
    DivisionResponse addDivision(DivisionRequest divisionRequest);
    DivisionResponse updateDivision(DivisionRequest divisionRequest);
    void deleteDivision(String divisionId);
    DivisionResponse getDivisionById(String divisionId);
    List<DivisionResponse> getAllDivisions();
    List<DivisionResponse> getAllDivisionsWhereActive();
}
