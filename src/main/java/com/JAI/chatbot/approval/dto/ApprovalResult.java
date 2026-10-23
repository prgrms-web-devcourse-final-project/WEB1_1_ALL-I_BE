package com.JAI.chatbot.approval.dto;

import java.util.List;
import java.util.UUID;

/** Completed results are stored in MySQL; replayed is response metadata only. */
public record ApprovalResult(String status, boolean replayed, List<ItemResult> items) {
    public record ItemResult(UUID proposalItemId, String type, UUID targetId) {}

    public static ApprovalResult completed(List<ItemResult> items) {
        return new ApprovalResult("COMPLETED", false, List.copyOf(items));
    }

    public static ApprovalResult rejected() {
        return new ApprovalResult("REJECTED", false, List.of());
    }

    public static ApprovalResult processing() {
        return new ApprovalResult("PROCESSING", false, List.of());
    }

    public ApprovalResult asReplay() {
        return new ApprovalResult(status, true, items);
    }
}
