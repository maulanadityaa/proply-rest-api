package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.EProcurementStatus;
import com.enigma.proplybackend.model.entity.Approval;
import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.Item;
import com.enigma.proplybackend.model.entity.ItemCategory;
import com.enigma.proplybackend.model.entity.Procurement;
import com.enigma.proplybackend.model.entity.ProcurementCategory;
import com.enigma.proplybackend.model.entity.ProcurementDetail;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ApprovalRequest;
import com.enigma.proplybackend.model.request.ProcurementApprovalRequest;
import com.enigma.proplybackend.model.request.ProcurementRequest;
import com.enigma.proplybackend.model.response.ApprovalResponse;
import com.enigma.proplybackend.model.response.ItemResponse;
import com.enigma.proplybackend.model.response.ProcurementCategoryResponse;
import com.enigma.proplybackend.model.response.ProcurementDetailResponse;
import com.enigma.proplybackend.model.response.ProcurementResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.ProcurementRepository;
import com.enigma.proplybackend.security.JwtUtil;
import com.enigma.proplybackend.service.ApprovalService;
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
    private final ApprovalService approvalService;

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
                    Item item = null;
                    ItemResponse itemResponse = null;
                    if (procurementDetailRequest.getItemId() != null) {
                        itemResponse = itemService.getItemById(procurementDetailRequest.getItemId());
                        item = Item.builder()
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
                    }

                    ProcurementDetail procurementDetail = ProcurementDetail.builder()
                            .procurement(procurement)
                            .item(item)
                            .quantity(procurementDetailRequest.getQuantity())
                            .build();
                    procurementDetailService.addProcurementDetail(procurementDetail);

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(procurementDetail.getId())
                            .itemResponse(itemResponse)
                            .quantity(procurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        List<ApprovalResponse> approvalResponses = new ArrayList<>();

        for (int i = 0; i < procurementRequest.getLevel(); i++) {
            UserResponse userApprovalResponse = userService.getUserById(procurementRequest.getApprovalRequests().get(i).getUserId());
            User userApproval = User.builder()
                    .id(userApprovalResponse.getUserId())
                    .fullName(userApprovalResponse.getFullName())
                    .gender(userApprovalResponse.getGender())
                    .birthDate(userApprovalResponse.getBirthDate())
                    .maritalStatus(userApprovalResponse.getMaritalStatus())
                    .division(Division.builder()
                            .id(userApprovalResponse.getDivisionResponse().getDivisionId())
                            .name(userApprovalResponse.getDivisionResponse().getName())
                            .isActive(userApprovalResponse.getDivisionResponse().getIsActive())
                            .build())
                    .isActive(true)
                    .build();

            Approval approval = Approval.builder()
                    .procurement(procurement)
                    .user(userApproval)
                    .status(EProcurementStatus.PENDING)
                    .build();
            approvalService.addApproval(approval);

            ApprovalResponse approvalResponse = ApprovalResponse.builder()
                    .userResponse(userApprovalResponse)
                    .status(approval.getStatus())
                    .notes(approval.getNotes() != null ? approval.getNotes() : "")
                    .build();
            approvalResponses.add(approvalResponse);
        }

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses);
    }

    @Override
    public ProcurementResponse getProcurementById(String procurementId) {
        Procurement procurement = procurementRepository.findById(procurementId).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementId + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = getProcurementDetailList(procurement);

        List<ApprovalResponse> approvalResponses = procurement.getApprovals().stream().map(
                approval -> {
                    UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());
                    return ApprovalResponse.builder()
                            .userResponse(userApprovalResponse)
                            .status(approval.getStatus())
                            .notes(approval.getNotes() != null ? approval.getNotes() : "")
                            .build();
                }
        ).toList();

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse approveProcurement(ProcurementApprovalRequest procurementApprovalRequest, String authorization) {
        Procurement procurement = procurementRepository.findById(procurementApprovalRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementApprovalRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = null;
                    if (procurementDetail.getItem() != null) {
                        itemResponse = itemService.getItemById(procurementDetail.getItem().getId());
                    }

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        List<ApprovalResponse> approvalResponses = new ArrayList<>();
        int counter = 0;

        for (Approval approval : procurement.getApprovals()) {
            UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());

            Map<String, String> userInfo = jwtUtil.getUserInfoByToken(authorization.substring(7));

            String email = userInfo.get("email");
            UserCredential userCredential = userCredentialService.getByEmail(email);
            UserResponse loginUserApproval = userService.getUserById(userCredential.getUser().getId());

            if (!userApprovalResponse.getUserId().equals(loginUserApproval.getUserId())) {
                ++counter;
                if (counter == procurement.getApprovals().size()) {
                    throw new ApplicationException("Bad user approval", "Only manager that corresponds to the same procurement can approve", HttpStatus.BAD_REQUEST);
                }

                approvalResponses.add(ApprovalResponse.builder()
                        .userResponse(userApprovalResponse)
                        .status(approval.getStatus())
                        .notes(approval.getNotes() != null ? approval.getNotes() : "")
                        .build());

                continue;
            }

            approval.setStatus(EProcurementStatus.APPROVED);

            approvalService.updateApproval(ApprovalRequest.builder()
                    .approvalId(approval.getId())
                    .userId(userApprovalResponse.getUserId())
                    .notes(approval.getNotes())
                    .status(approval.getStatus())
                    .build());
            approvalResponses.add(ApprovalResponse.builder()
                    .userResponse(userApprovalResponse)
                    .status(approval.getStatus())
                    .notes(approval.getNotes() != null ? approval.getNotes() : "")
                    .build());
        }

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse rejectProcurement(ProcurementApprovalRequest procurementApprovalRequest, String authorization) {
        Procurement procurement = procurementRepository.findById(procurementApprovalRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementApprovalRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = null;
                    if (procurementDetail.getItem() != null) {
                        itemResponse = itemService.getItemById(procurementDetail.getItem().getId());
                    }

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        List<ApprovalResponse> approvalResponses = new ArrayList<>();

        int counter = 0;

        for (Approval approval : procurement.getApprovals()) {
            UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());

            Map<String, String> userInfo = jwtUtil.getUserInfoByToken(authorization.substring(7));

            String email = userInfo.get("email");
            UserCredential userCredential = userCredentialService.getByEmail(email);
            UserResponse loginUserApproval = userService.getUserById(userCredential.getUser().getId());

            if (!userApprovalResponse.getUserId().equals(loginUserApproval.getUserId())) {
                ++counter;
                if (counter == procurement.getApprovals().size()) {
                    throw new ApplicationException("Bad user approval", "Only manager that corresponds to the same procurement can approve", HttpStatus.BAD_REQUEST);
                }

                approvalResponses.add(ApprovalResponse.builder()
                        .userResponse(userApprovalResponse)
                        .status(approval.getStatus())
                        .notes(approval.getNotes() != null ? approval.getNotes() : "")
                        .build());

                continue;
            }

            approval.setStatus(EProcurementStatus.REJECTED);
            approval.setNotes(procurementApprovalRequest.getNotes() != null ? procurementApprovalRequest.getNotes() : "");

            approvalService.updateApproval(ApprovalRequest.builder()
                    .approvalId(approval.getId())
                    .userId(userApprovalResponse.getUserId())
                    .notes(approval.getNotes())
                    .status(approval.getStatus())
                    .build());
            approvalResponses.add(ApprovalResponse.builder()
                    .userResponse(userApprovalResponse)
                    .status(approval.getStatus())
                    .notes(approval.getNotes() != null ? approval.getNotes() : "")
                    .build());
        }

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProcurementResponse cancelProcurement(ProcurementApprovalRequest procurementApprovalRequest) {
        Procurement procurement = procurementRepository.findById(procurementApprovalRequest.getProcurementId()).orElseThrow(() -> new ApplicationException("Could not find procurement", "Procurement with id=" + procurementApprovalRequest.getProcurementId() + " not found", HttpStatus.NOT_FOUND));

        ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());

        UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

        List<ProcurementDetailResponse> procurementDetailResponses = procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = itemService.getItemById(procurementDetail.getItem().getId());

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();

        List<ApprovalResponse> approvalResponses = procurement.getApprovals().stream().map(
                approval -> {
                    UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());
                    approval.setStatus(EProcurementStatus.CANCELED);

                    approvalService.updateApproval(ApprovalRequest.builder()
                            .approvalId(approval.getId())
                            .userId(userApprovalResponse.getUserId())
                            .notes(approval.getNotes())
                            .status(approval.getStatus())
                            .build());

                    return ApprovalResponse.builder()
                            .userResponse(userApprovalResponse)
                            .status(approval.getStatus())
                            .notes(approval.getNotes() != null ? approval.getNotes() : "")
                            .build();
                }
        ).toList();

        procurement.setUpdatedAt(Instant.now().toEpochMilli());

        return toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses);
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
            List<ApprovalResponse> approvalResponses = procurement.getApprovals().stream().map(
                    approval -> {
                        UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());

                        return ApprovalResponse.builder()
                                .userResponse(userApprovalResponse)
                                .status(approval.getStatus())
                                .notes(approval.getNotes() != null ? approval.getNotes() : "")
                                .build();
                    }
            ).toList();

            procurementResponses.add(toProcurementResponse(procurement, procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId()), getProcurementDetailList(procurement), userService.getUserById(procurement.getUser().getId()), approvalResponses));
        }

        return new PageImpl<>(procurementResponses, pageable, procurementPage.getTotalElements());
    }

    @Override
    public List<ProcurementResponse> getAllProcurementsByUserId(String userId) {
        List<Procurement> procurements = procurementRepository.findAllByUser_Id(userId).orElseThrow(() -> new ApplicationException("No procurements found", null, HttpStatus.NOT_FOUND));
        List<ProcurementResponse> procurementResponses = new ArrayList<>();

        if (procurements.isEmpty()) throw new ApplicationException("No procurements found", null, HttpStatus.NOT_FOUND);

        for (Procurement procurement : procurements) {
            List<ApprovalResponse> approvalResponses = procurement.getApprovals().stream().map(
                    approval -> {
                        UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());

                        return ApprovalResponse.builder()
                                .userResponse(userApprovalResponse)
                                .status(approval.getStatus())
                                .notes(approval.getNotes() != null ? approval.getNotes() : "")
                                .build();
                    }
            ).toList();

            procurementResponses.add(toProcurementResponse(procurement, procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId()), getProcurementDetailList(procurement), userService.getUserById(procurement.getUser().getId()), approvalResponses));
        }

        return getProcurementResponses(procurements, procurementResponses);
    }

    private List<ProcurementResponse> getProcurementResponses(List<Procurement> procurements, List<ProcurementResponse> procurementResponses) {
        for (Procurement procurement : procurements) {
            ProcurementCategoryResponse procurementCategoryResponse = procurementCategoryService.getProcurementCategoryById(procurement.getProcurementCategory().getId());
            UserResponse userResponse = userService.getUserById(procurement.getUser().getId());

            List<ProcurementDetailResponse> procurementDetailResponses = getProcurementDetailList(procurement);

            List<ApprovalResponse> approvalResponses = procurement.getApprovals().stream().map(
                    approval -> {
                        UserResponse userApprovalResponse = userService.getUserById(approval.getUser().getId());

                        return ApprovalResponse.builder()
                                .userResponse(userApprovalResponse)
                                .status(approval.getStatus())
                                .notes(approval.getNotes() != null ? approval.getNotes() : "")
                                .build();
                    }
            ).toList();

            procurementResponses.add(toProcurementResponse(procurement, procurementCategoryResponse, procurementDetailResponses, userResponse, approvalResponses));
        }

        return procurementResponses;
    }

    private static ProcurementResponse toProcurementResponse(Procurement procurement, ProcurementCategoryResponse procurementCategoryResponse, List<ProcurementDetailResponse> procurementDetailResponses, UserResponse userResponse, List<ApprovalResponse> approvalResponses) {
        return ProcurementResponse.builder()
                .procurementId(procurement.getId())
                .userResponse(userResponse)
                .procurementCategoryResponse(procurementCategoryResponse)
                .procurementDetailResponses(procurementDetailResponses)
                .approvalResponses(approvalResponses)
                .createdAt(procurement.getCreatedAt())
                .updatedAt(procurement.getUpdatedAt())
                .build();
    }

    private List<ProcurementDetailResponse> getProcurementDetailList(Procurement procurement) {
        return procurement.getProcurementDetails().stream().map(
                procurementDetail -> {
                    ItemResponse itemResponse = null;
                    if (procurementDetail.getItem() != null) {
                        itemResponse = itemService.getItemById(procurementDetail.getItem().getId());
                    }

                    ProcurementDetail existProcurementDetail = procurementDetailService.getProcurementDetailById(procurementDetail.getId());

                    return ProcurementDetailResponse.builder()
                            .procurementDetailId(existProcurementDetail.getId())
                            .itemResponse(itemResponse)
                            .quantity(existProcurementDetail.getQuantity())
                            .build();
                }
        ).toList();
    }
}
