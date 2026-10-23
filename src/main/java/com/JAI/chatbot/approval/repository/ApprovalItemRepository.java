package com.JAI.chatbot.approval.repository;

import com.JAI.chatbot.approval.domain.ApprovalItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApprovalItemRepository extends JpaRepository<ApprovalItem, UUID> {
}
