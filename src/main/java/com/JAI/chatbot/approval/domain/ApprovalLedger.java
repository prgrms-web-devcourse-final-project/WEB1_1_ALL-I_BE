package com.JAI.chatbot.approval.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chatbot_approval_ledger", indexes = @Index(name = "idx_approval_token", columnList = "approval_token"), uniqueConstraints =
        @UniqueConstraint(name = "uk_approval_user_token", columnNames = {"user_id", "approval_token"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLedger {
    @Id
    @Column(name = "approval_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID approvalId;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "approval_token", nullable = false, length = 36)
    private String approvalToken;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(name = "proposal_hash", nullable = false, length = 64)
    private String proposalHash;

    @Column(name = "decision", nullable = false, length = 10)
    private String decision;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "result_json", columnDefinition = "TEXT")
    private String resultJson;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static ApprovalLedger processing(UUID userId, String token, String requestHash,
                                            String proposalHash, boolean accepted) {
        ApprovalLedger ledger = new ApprovalLedger();
        ledger.approvalId = UUID.randomUUID();
        ledger.userId = userId;
        ledger.approvalToken = token;
        ledger.requestHash = requestHash;
        ledger.proposalHash = proposalHash;
        ledger.decision = accepted ? "ACCEPTED" : "REJECTED";
        ledger.status = "PROCESSING";
        return ledger;
    }

    public void complete(String resultJson) {
        this.resultJson = resultJson;
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }
}
