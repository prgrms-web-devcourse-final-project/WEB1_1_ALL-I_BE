package com.JAI.chatbot.approval.service;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.approval.domain.ApprovalLedger;
import com.JAI.chatbot.approval.dto.ApprovalResult;
import com.JAI.chatbot.approval.redis.ApprovalRedisRepository;
import com.JAI.chatbot.approval.repository.ApprovalLedgerRepository;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalOrchestratorTest {
    @Mock ApprovalLedgerRepository ledgerRepository;
    @Mock ApprovalPersistenceService persistenceService;
    @Mock ApprovalRedisRepository redisRepository;
    @Mock RedisChatbotUtil chatbotUtil;

    ApprovalOrchestrator orchestrator;
    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    final UUID userId = UUID.randomUUID();
    final String token = UUID.randomUUID().toString();

    @BeforeEach
    void init() {
        orchestrator = new ApprovalOrchestrator(
                ledgerRepository, persistenceService, redisRepository, chatbotUtil, mapper);
    }

    @Test
    void completedApprovalReturnsExistingResultWithoutRedisOrWrites() throws Exception {
        ApprovalLedger ledger = ApprovalLedger.processing(userId, token,
                hash("accept=true|alarm=false"), "proposalHash", true);
        ledger.complete(mapper.writeValueAsString(ApprovalResult.completed(List.of(
                new ApprovalResult.ItemResult(UUID.randomUUID(), "TODO", UUID.randomUUID())))));
        when(ledgerRepository.findByUserIdAndApprovalToken(userId, token))
                .thenReturn(Optional.of(ledger));

        ApprovalResult replay = orchestrator.process(userId, token, true, false);

        assertEquals("COMPLETED", replay.status());
        assertTrue(replay.replayed());
        assertEquals(1, replay.items().size());
        verifyNoInteractions(redisRepository, persistenceService);
    }

    @Test
    void aTokenCannotBeReusedForAChangedApprovalDecision() {
        ApprovalLedger ledger = ApprovalLedger.processing(userId, token,
                hash("accept=true|alarm=false"), "proposalHash", true);
        ledger.complete("{}");
        when(ledgerRepository.findByUserIdAndApprovalToken(userId, token))
                .thenReturn(Optional.of(ledger));

        assertThrows(ResponseStatusException.class,
                () -> orchestrator.process(userId, token, false, false));
        verifyNoInteractions(redisRepository, persistenceService);
    }

    @Test
    void busyTokenReturnsProcessingWithoutEnteringTransaction() {
        when(ledgerRepository.findByUserIdAndApprovalToken(userId, token))
                .thenReturn(Optional.empty());
        when(redisRepository.claim(eq(token), eq(userId), any(UUID.class)))
                .thenReturn(new ApprovalRedisRepository.Claim(
                        ApprovalRedisRepository.Status.BUSY, null));

        ApprovalResult result = orchestrator.process(userId, token, true, false);

        assertEquals("PROCESSING", result.status());
        verifyNoInteractions(persistenceService);
    }

    @Test
    void successfulCommitCleansUpRedisAndReleasesTheLease() throws Exception {
        when(ledgerRepository.findByUserIdAndApprovalToken(userId, token))
                .thenReturn(Optional.empty());
        ChatbotRedisDataDTO rawSnapshot = ChatbotRedisDataDTO.builder()
                .token(token).userId(userId).build();
        ChatbotRedisDataDTO snapshot = com.JAI.chatbot.approval.dto.ProposalIdentity.ready(rawSnapshot, mapper);
        when(redisRepository.claim(eq(token), eq(userId), any(UUID.class)))
                .thenReturn(new ApprovalRedisRepository.Claim(
                        ApprovalRedisRepository.Status.ACQUIRED, snapshot));
        when(persistenceService.persist(any(), eq(snapshot)))
                .thenReturn(ApprovalResult.completed(List.of()));
        when(redisRepository.cleanupAfterCommit(eq(token), any(UUID.class)))
                .thenReturn(true);

        ApprovalResult result = orchestrator.process(userId, token, true, false);

        assertEquals("COMPLETED", result.status());
        verify(redisRepository).cleanupAfterCommit(eq(token), any(UUID.class));
        verify(chatbotUtil).deleteChatbotData(token);
        verify(redisRepository).release(eq(token), any(UUID.class));
    }

    @Test
    void failureDoesNotDeleteProposalAndReleasesLease() throws Exception {
        when(ledgerRepository.findByUserIdAndApprovalToken(userId, token))
                .thenReturn(Optional.empty());
        ChatbotRedisDataDTO rawSnapshot = ChatbotRedisDataDTO.builder()
                .token(token).userId(userId).build();
        ChatbotRedisDataDTO snapshot = com.JAI.chatbot.approval.dto.ProposalIdentity.ready(rawSnapshot, mapper);
        when(redisRepository.claim(eq(token), eq(userId), any(UUID.class)))
                .thenReturn(new ApprovalRedisRepository.Claim(
                        ApprovalRedisRepository.Status.ACQUIRED, snapshot));
        when(persistenceService.persist(any(), eq(snapshot)))
                .thenThrow(new IllegalStateException("database failed"));

        assertThrows(IllegalStateException.class,
                () -> orchestrator.process(userId, token, true, false));
        verify(redisRepository, never()).cleanupAfterCommit(anyString(), any());
        verify(chatbotUtil, never()).deleteChatbotData(anyString());
        verify(redisRepository).release(eq(token), any(UUID.class));
    }

    @Test
    void newRequestFailsSafelyWhenRedisIsUnavailable() {
        when(redisRepository.claim(eq(token),eq(userId),any()))
                .thenThrow(new org.springframework.data.redis.RedisConnectionFailureException("offline"));
        ResponseStatusException error=assertThrows(ResponseStatusException.class,
                () -> orchestrator.process(userId,token,true,false));
        assertEquals(503,error.getStatusCode().value());
        verifyNoInteractions(persistenceService);
    }

    @Test
    void invalidTokenNeverAccessesStorage() {
        assertEquals(400,assertThrows(ResponseStatusException.class,
                () -> orchestrator.process(userId,"invalid",true,false)).getStatusCode().value());
        verifyNoInteractions(ledgerRepository,redisRepository,persistenceService);
    }

    @Test
    void uniqueConflictIsReplayedOutsideFailedTransaction() throws Exception {
        ChatbotRedisDataDTO snapshot=com.JAI.chatbot.approval.dto.ProposalIdentity.ready(
                ChatbotRedisDataDTO.builder().userId(userId).token(token).build(),mapper);
        ApprovalLedger ledger=ApprovalLedger.processing(userId,token,hash("accept=true|alarm=false"),"p",true);
        ledger.complete(mapper.writeValueAsString(ApprovalResult.completed(List.of())));
        when(ledgerRepository.findByUserIdAndApprovalToken(userId,token))
                .thenReturn(Optional.empty(),Optional.of(ledger));
        when(redisRepository.claim(eq(token),eq(userId),any())).thenReturn(
                new ApprovalRedisRepository.Claim(ApprovalRedisRepository.Status.ACQUIRED,snapshot));
        when(persistenceService.persist(any(),eq(snapshot)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("duplicate"));
        assertTrue(orchestrator.process(userId,token,true,false).replayed());
        verify(redisRepository).release(eq(token),any());
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
