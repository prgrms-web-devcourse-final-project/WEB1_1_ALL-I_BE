package com.JAI.chatbot.approval.dto;

import java.util.UUID;

public record ApprovalCommand(UUID userId, String token, boolean accept, boolean alarm,
                              String requestHash, String proposalHash) {}
