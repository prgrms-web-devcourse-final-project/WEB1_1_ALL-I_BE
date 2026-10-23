package com.JAI.chatbot.approval.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "chatbot_approval_item", uniqueConstraints =
        @UniqueConstraint(name = "uk_approval_proposal_item", columnNames = {"approval_id", "proposal_item_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalItem {
    @Id
    @Column(name = "approval_item_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID approvalItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approval_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_approval_item_ledger"))
    private ApprovalLedger ledger;

    @Column(name = "proposal_item_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID proposalItemId;

    @Column(name = "target_type", nullable = false, length = 10)
    private String targetType;

    @Column(name = "target_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID targetId;

    public static ApprovalItem of(ApprovalLedger ledger, UUID proposalItemId,
                                  String targetType, UUID targetId) {
        ApprovalItem item = new ApprovalItem();
        item.approvalItemId = UUID.randomUUID();
        item.ledger = ledger;
        item.proposalItemId = proposalItemId;
        item.targetType = targetType;
        item.targetId = targetId;
        return item;
    }
}
