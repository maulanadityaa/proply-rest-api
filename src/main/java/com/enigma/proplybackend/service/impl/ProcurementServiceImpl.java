package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.EProcurementStatus;
import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.Item;
import com.enigma.proplybackend.model.entity.ItemCategory;
import com.enigma.proplybackend.model.entity.Procurement;
import com.enigma.proplybackend.model.entity.ProcurementCategory;
import com.enigma.proplybackend.model.entity.ProcurementDetail;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ItemResponse;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;
import com.enigma.proplybackend.model.response.ProcurementDetailResponse;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.ProcurementRepository;
import com.enigma.proplybackend.service.ItemService;
import com.enigma.proplybackend.service.ProcurementCategoryService;
import com.enigma.proplybackend.service.ProcurementDetailService;
import com.enigma.proplybackend.service.ProcurementService;
import com.enigma.proplybackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcurementServiceImpl implements ProcurementService {
    private final ProcurementRepository procurementRepository;
    private final ProcurementDetailService procurementDetailService;
    private final UserService userService;
    private final ProcurementCategoryService procurementCategoryService;
    private final ItemService itemService;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse addProcurement(ProcurementRequest procurementRequest) {
        UserResponse userResponse = userService.getUserById(procurementRequest.getUserId());
        User user = User.builder()
                .id(userResponse.getUserId())
                .fullName(userResponse.getFullName())
                .gender(userResponse.getGender())
                .birthDate(userResponse.getBirthDate())
                .maritalStatus(userResponse.getMaritalStatus())
                .division(Division.builder()
                        .id(userResponse.getDivisionResponse().getDivisionId())
                        .name(userResponse.getDivisionResponse().getName())
                        .isActive(userResponse.getDivisionResponse().getIsActive())
                        .build())
                .isActive(true)
                .build();

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurementRequest.getProcurementCategoryId());
        ProcurementCategory procurementCategory = ProcurementCategory.builder()
                .id(procurementCategoryResponse.getProcurementCategoryId())
                .name(procurementCategoryResponse.getName())
                .isActive(procurementCategoryResponse.getIsActive())
                .build();

        Procurement procurement = Procurement.builder()
                .user(user)
                .procurementCategory(procurementCategory)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();
        procurementRepository.saveAndFlush(procurement);

        List<ProcurementDetailResponse> procurementDetailResponses = procurementRequest.getProcurementDetailRequests().stream().map(
                procurementDetailRequest -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetailRequest.getItemId());
                    Item item = Item.builder()
                            .id(itemResponse.getItemId())
                            .name(itemResponse.getName())
                            .itemCategory(ItemCategory.builder()
                                    .id(itemResponse.getItemCategoryResponse().getItemCategoryId())
                                    .name(itemResponse.getItemCategoryResponse().getName())
                                    .isActive(itemResponse.getItemCategoryResponse().getIsActive())
                                    .build())
                            .createdAt(itemResponse.getCreatedAt())
                            .updatedAt(itemResponse.getUpdatedAt())
                            .build();
                    System.out.println(procurementDetailRequest.getQuantity());

                    ProcurementDetail procurementDetail = ProcurementDetail.builder()
                            .procurement(procurement)
                            .status(EProcurementStatus.PENDING)
                            .item(item)
                            .quantity(procurementDetailRequest.getQuantity())
                            .build();
                    procurementDetailService.addProcurementDetail(procurementDetail);

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(procurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(procurementDetail.getStatus())
                            .quantity(procurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Override
    public ProcurementResponse getProcurementById(String procurementId) {
        Procurement procurement = procurementRepository.findById(procurementId).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementId + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = getProcurementDetailList(procurement);

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Override
    public ProcurementResponse approveProcurement(ProcurementDetailRequest procurementDetailRequest) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    if (procurementDetail.getId().equals(procurementDetailRequest.getId())) {
                        existProcurementDetail = procurementDetailService.updateStatusProcurementDetail(procurementDetail.getId(), EProcurementStatus.APPROVED);
                    }

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Override
    public ProcurementResponse rejectProcurement(ProcurementDetailRequest procurementDetailRequest) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    if (procurementDetail.getId().equals(procurementDetailRequest.getId())) {
                        existProcurementDetail = procurementDetailService.updateStatusProcurementDetail(procurementDetail.getId(), EProcurementStatus.REJECTED);
                    }

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Override
    public ProcurementResponse cancelProcurement(ProcurementDetailRequest procurementDetailRequest) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    if (procurementDetail.getId().equals(procurementDetailRequest.getId())) {
                        existProcurementDetail = procurementDetailService.updateStatusProcurementDetail(procurementDetail.getId(), EProcurementStatus.CANCELED);
                    }

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Override
    public List<ProcurementResponse> getAllProcurements() {
        List<Procurement> procurements = procurementRepository.findAll();
        List<ProcurementResponse> procurementResponses = new ArrayList<>();

        if (procurements.isEmpty()) throw new ApplicationException("No procurements found", null, HttpStatus.NOT_FOUND);

        return getProcurementResponses(procurements, procurementResponses);
    }

    @Override
    public List<ProcurementResponse> getAllProcurementsByUserId(String userId) {
        List<Procurement> procurements = procurementRepository.findAllByUser_Id(userId).orElseThrow(() -> new ApplicationException("No procurements found", null, HttpStatus.NOT_FOUND));
        List<ProcurementResponse> procurementResponses = new ArrayList<>();


        return getProcurementResponses(procurements, procurementResponses);
    }

    private List<ProcurementResponse> getProcurementResponses(List<Procurement> procurements, List<ProcurementResponse> procurementResponses) {
        for (Procurement procurement : procurements) {
            ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());
            UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

            List<ProcurementDetailResponse> procurementDetailResponses = getProcurementDetailList(procurement);

            procurementResponses.add(toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse));
        }

        return procurementResponses;
    }

    private static ProcurementResponse toProcurementResponse(Procurement procurement, ProcurementCategoryResponse procurementCategoryResponse, List<ProcurementDetailResponse> procurementDetailResponses, UserResponse userResponse) {
        return ProcurementResponse.builder()
                .procurementId(procurement.getId())
                .userResponse(userResponse)
                .procurementCategoryResponse(procurementCategoryResponse)
                .procurementDetailResponses(procurementDetailResponses)
                .createdAt(procurement.getCreatedAt())
                .updatedAt(procurement.getUpdatedAt())
                .build();
    }

    private List<ProcurementDetailResponse> getProcurementDetailList(Procurement procurement) {
        return procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();
    }
}
