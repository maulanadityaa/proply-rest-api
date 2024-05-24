package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.EProcurementStatus;
import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.Item;
import com.enigma.proplybackend.model.entity.ItemCategory;
import com.enigma.proplybackend.model.entity.Procurement;
import com.enigma.proplybackend.model.entity.ProcurementCategory;
import com.enigma.proplybackend.model.entity.ProcurementDetail;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ProcurementDetailRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ItemResponse;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;
import com.enigma.proplybackend.model.response.ProcurementDetailResponse;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.ProcurementRepository;
import com.enigma.proplybackend.security.JwtUtil;
import com.enigma.proplybackend.service.ItemService;
import com.enigma.proplybackend.service.ProcurementCategoryService;
import com.enigma.proplybackend.service.ProcurementDetailService;
import com.enigma.proplybackend.service.ProcurementService;
import com.enigma.proplybackend.service.UserCredentialService;
import com.enigma.proplybackend.service.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcurementServiceImpl implements ProcurementService {
    private final ProcurementRepository procurementRepository;
    private final ProcurementDetailService procurementDetailService;
    private final UserService userService;
    private final ProcurementCategoryService procurementCategoryService;
    private final ItemService itemService;
    private final UserCredentialService userCredentialService;
    private final JwtUtil jwtUtil;

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

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse approveProcurement(ProcurementDetailRequest procurementDetailRequest, String authorization) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    UserResponse userApprovalResponse = null;

                    if (procurementDetail.getId().equals(procurementDetailRequest.getProcurementDetailId())) {
                        Map<String, String> userInfo = jwtUtil.getUserInfoByToken(authorization.substring(7));

                        String email = userInfo.get("email");
                        UserCredential userCredential = userCredentialService.getByEmail(email);
                        userApprovalResponse = userService.getUserById(userCredential.getUser().getId());

                        existProcurementDetail = procurementDetailService.updateStatusProcurementDetail(procurementDetail.getId(), EProcurementStatus.APPROVED);
                        existProcurementDetail.setApprovedAt(Instant.now().toEpochMilli());
                        existProcurementDetail.setApprovedBy(userApprovalResponse.getUserId());

                        procurementDetailService.addProcurementDetail(existProcurementDetail);
                    }

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .approvedAt(existProcurementDetail.getApprovedAt())
                            .approvedBy(userApprovalResponse)
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse rejectProcurement(ProcurementDetailRequest procurementDetailRequest, String authorization) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    if (procurementDetail.getId().equals(procurementDetailRequest.getProcurementDetailId())) {
                        existProcurementDetail.setNotes(procurementDetailRequest.getNotes());
                        existProcurementDetail = procurementDetailService.updateStatusProcurementDetail(procurementDetail.getId(), EProcurementStatus.REJECTED);

                        procurementDetailService.addProcurementDetail(existProcurementDetail);
                    }

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .status(existProcurementDetail.getStatus())
                            .quantity(existProcurementDetail.getQuantity())
                            .notes(existProcurementDetail.getNotes())
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse cancelProcurement(ProcurementDetailRequest procurementDetailRequest) {
        Procurement procurement = procurementRepository.findById(procurementDetailRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementDetailRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    if (procurementDetail.getId().equals(procurementDetailRequest.getProcurementDetailId())) {
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
    public Page<ProcurementResponse> getAllByNameOrCategory(String name, String category, Integer page, Integer size) {
        Specification<Procurement> specification = (root, query, criteriaBuilder) -> {
            Join<Procurement, ProcurementCategory> procurementCategories = root.join("procurementCategory");
            Join<Procurement, User> users = root.join("user");

            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(users.get("fullName")), "%" + name.toLowerCase() + "%"));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(procurementCategories.get("name")), "%" + category.toLowerCase() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Procurement> procurementPage = procurementRepository.findAll(specification, pageable);
        List<ProcurementResponse> procurementResponses = new ArrayList<>();

        for (Procurement procurement : procurementPage.getContent()) {
            procurementResponses.add(toProcurementResponse(procurement, procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId()), getProcurementDetailList(procurement), userService.getUserById(procurement.getUser().getId())));
        }

        return new PageImpl<>(procurementResponses, pageable, procurementPage.getTotalElements());
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
