package com.JAI.chatbot.approval.service;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.approval.domain.ApprovalLedger;
import com.JAI.chatbot.approval.dto.ApprovalCommand;
import com.JAI.chatbot.approval.dto.ApprovalResult;
import com.JAI.chatbot.approval.redis.ApprovalRedisRepository;
import com.JAI.chatbot.approval.repository.ApprovalLedgerRepository;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalOrchestrator {
    private final ApprovalLedgerRepository ledgerRepository;
    private final ApprovalPersistenceService persistenceService;
    private final ApprovalRedisRepository approvalRedisRepository;
    private final RedisChatbotUtil redisChatbotUtil;
    private final ObjectMapper objectMapper;

    /** This method deliberately does NOT have @Transactional. */
    public ApprovalResult process(UUID userId, String token, boolean accept, boolean alarm) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인 토큰은 필수입니다");
        }
        try {
            if (!UUID.fromString(token).toString().equals(token)) throw new IllegalArgumentException();
        } catch (IllegalArgumentException invalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 UUID 토큰이 필요합니다");
        }
        String requestHash = sha256("accept=" + accept + "|alarm=" + (accept && alarm));
        Optional<ApprovalLedger> prior = ledgerRepository.findByUserIdAndApprovalToken(userId, token);
        if (prior.isPresent()) return replay(prior.get(), requestHash);

        if (ledgerRepository.existsByApprovalTokenAndUserIdNot(token, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 승인 토큰입니다");
        }
        UUID leaseOwner = UUID.randomUUID();
        ApprovalRedisRepository.Claim claim;
        try {
            claim = approvalRedisRepository.claim(token, userId, leaseOwner);
        } catch (DataAccessException redisFailure) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Redis 승인 저장소를 사용할 수 없습니다", redisFailure);
        }
        switch (claim.status()) {
            case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 승인 토큰입니다");
            case NOT_READY -> throw new ResponseStatusException(HttpStatus.CONFLICT, "추천안이 완성되지 않았습니다");
            case MISSING -> {
                // A commit can race the Redis cleanup: consult the durable source again.
                return ledgerRepository.findByUserIdAndApprovalToken(userId, token)
                        .map(l -> replay(l, requestHash))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, "만료된 추천안입니다"));
            }
            case BUSY -> {
                return ledgerRepository.findByUserIdAndApprovalToken(userId, token)
                        .map(l -> replay(l, requestHash))
                        .orElseGet(ApprovalResult::processing);
            }
            case ACQUIRED -> { /* continue */ }
        }

        boolean committed = false;
        try {
            ChatbotRedisDataDTO snapshot = claim.proposal();
            String proposalHash = snapshot.getProposalHash();
            if (proposalHash == null || !proposalHash.equals(com.JAI.chatbot.approval.dto.ProposalIdentity.hash(snapshot, objectMapper))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "추천안 무결성 검증에 실패했습니다");
            }
            ApprovalCommand command = new ApprovalCommand(
                    userId, token, accept, alarm, requestHash, proposalHash);
            ApprovalResult result;
            try {
                result = persistenceService.persist(command, snapshot);
            } catch (DataIntegrityViolationException duplicateOrConstraintViolation) {
                // The competing transaction has already committed, or this was a different constraint failure.
                Optional<ApprovalLedger> existing = ledgerRepository.findByUserIdAndApprovalToken(userId, token);
                if (existing.isPresent()) return replay(existing.get(), requestHash);
                throw duplicateOrConstraintViolation;
            }
            committed = true; // Only set after the transaction proxy has returned successfully.
            try {
                if (approvalRedisRepository.cleanupAfterCommit(token, leaseOwner)) {
                    redisChatbotUtil.deleteChatbotData(token); // legacy payload and token->key mapping
                }
            } catch (RuntimeException cleanupFailure) {
                // Completion is durable: clients will use the Ledger on retry.
                log.warn("Approval Redis cleanup failed after DB commit: {}", cleanupFailure.toString());
            }
            return result;
        } finally {
            try {
                approvalRedisRepository.release(token, leaseOwner);
            } catch (RuntimeException releaseFailure) {
                // Do not turn a committed DB success into an HTTP error.
                log.warn("Approval Redis lease release failed (committed={}): {}",
                        committed, releaseFailure.toString());
            }
        }
    }

    private ApprovalResult replay(ApprovalLedger ledger, String requestHash) {
        if (!ledger.getRequestHash().equals(requestHash)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 다른 결정으로 처리된 토큰입니다");
        }
        if (!"COMPLETED".equals(ledger.getStatus())) return ApprovalResult.processing();
        try {
            return objectMapper.readValue(ledger.getResultJson(), ApprovalResult.class).asReplay();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("저장된 승인 결과를 읽지 못했습니다", e);
        }
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다", e);
        }
    }
}
