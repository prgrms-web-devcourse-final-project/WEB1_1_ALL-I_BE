package com.JAI.chatbot.approval.repository;

import com.JAI.chatbot.approval.domain.ApprovalLedger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

/** Fast H2 uniqueness regression. Re-run against real MySQL before rollout. */
@DataJpaTest
class ApprovalLedgerRepositoryTest {
    @Autowired ApprovalLedgerRepository repository;

    @Test
    void sameUserAndTokenCannotBePersistedTwice() {
        UUID userId = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        repository.saveAndFlush(ApprovalLedger.processing(userId, token, "a", "b", true));

        assertThrows(DataIntegrityViolationException.class, () ->
                repository.saveAndFlush(ApprovalLedger.processing(userId, token, "a", "b", true)));
    }
}
