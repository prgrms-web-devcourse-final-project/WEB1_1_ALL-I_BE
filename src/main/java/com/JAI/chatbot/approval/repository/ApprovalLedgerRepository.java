package com.JAI.chatbot.approval.repository;

import com.JAI.chatbot.approval.domain.ApprovalLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApprovalLedgerRepository extends JpaRepository<ApprovalLedger, UUID> {
    boolean existsByApprovalTokenAndUserIdNot(String approvalToken, UUID userId);

    Optional<ApprovalLedger> findByUserIdAndApprovalToken(UUID userId, String approvalToken);
}
