package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Approval;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ApprovalRequest;
import com.enigma.proplybackend.repository.ApprovalRepository;
import com.enigma.proplybackend.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRepository;

    @Override
    public Approval addApproval(Approval approval) {
        return approvalRepository.save(approval);
    }

    @Override
    public Approval updateApproval(ApprovalRequest approvalRequest) {
        Approval approval = approvalRepository.findById(approvalRequest.getApprovalId()).orElseThrow(() -> new ApplicationException("Approval not found", "Approval with id=" + approvalRequest.getApprovalId() + "not found", HttpStatus.NOT_FOUND));

        approval.setStatus(approvalRequest.getStatus());
        approval.setNotes(approvalRequest.getNotes());
        approvalRepository.save(approval);

        return approval;
    }
}
