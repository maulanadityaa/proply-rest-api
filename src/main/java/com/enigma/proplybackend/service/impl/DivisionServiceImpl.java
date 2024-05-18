package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.request.DivisionRequest;
import com.enigma.proplybackend.model.response.DivisionResponse;
import com.enigma.proplybackend.repository.DivisionRepository;
import com.enigma.proplybackend.service.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {
    private final DivisionRepository divisionRepository;

    @Override
    public DivisionResponse addDivision(DivisionRequest divisionRequest) {
        try {
            Division division = toDivision(divisionRequest);
            divisionRepository.save(division);

            return toDivisionResponse(division);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DivisionResponse updateDivision(DivisionRequest divisionRequest) {
        Division division = divisionRepository.findById(divisionRequest.getId()).orElse(null);

        try {
            if (division != null) {
                division.setName(divisionRequest.getName());
                divisionRepository.save(division);

                return toDivisionResponse(division);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteDivision(String divisionId) {
        Division division = divisionRepository.findById(divisionId).orElse(null);

        try {
            if (division != null) {
                division.setIsActive(false);
                divisionRepository.save(division);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DivisionResponse getDivisionById(String divisionId) {
        Division division = divisionRepository.findById(divisionId).orElse(null);

        try {
            if (division != null) {
                return toDivisionResponse(division);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DivisionResponse> getAllDivisions() {
        List<Division> divisionList = divisionRepository.findAll();
        List<DivisionResponse> divisionResponseList = new ArrayList<>();

        try {
            for (Division division : divisionList) {
                divisionResponseList.add(toDivisionResponse(division));
            }

            return divisionResponseList;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<DivisionResponse> getAllDivisionsWhereActive() {
        List<Division> divisionList = divisionRepository.findAll();
        List<DivisionResponse> divisionResponseList = new ArrayList<>();

        try {
            for (Division division : divisionList) {
                if (division.getIsActive()) {
                    divisionResponseList.add(toDivisionResponse(division));
                }
            }

            return divisionResponseList;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static DivisionResponse toDivisionResponse(Division division) {
        return DivisionResponse.builder()
                .divisionId(division.getId())
                .name(division.getName())
                .isActive(division.getIsActive())
                .build();
    }

    private static Division toDivision(DivisionRequest divisionRequest) {
        return Division.builder()
                .name(divisionRequest.getName())
                .isActive(true)
                .build();
    }
}
