package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.entity.Approval;
import com.enigma.proplybackend.model.request.ApprovalRequest;

public interface ApprovalService {
    Approval addApproval(Approval approval);

    Approval updateApproval(ApprovalRequest approvalRequest);
}
