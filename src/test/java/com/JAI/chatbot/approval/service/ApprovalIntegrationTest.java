package com.JAI.chatbot.approval.service;

import com.JAI.chatbot.RedisChatbotUtil;
import com.JAI.chatbot.approval.dto.*;
import com.JAI.chatbot.approval.repository.*;
import com.JAI.chatbot.approval.redis.ApprovalRedisRepository;
import com.JAI.chatbot.controller.dto.*;
import com.JAI.chatbot.controller.dto.request.*;
import com.JAI.chatbot.controller.dto.response.*;
import com.JAI.category.domain.Category;
import com.JAI.category.repository.CategoryRepository;
import com.JAI.user.domain.*;
import com.JAI.user.repository.UserRepository;
import com.JAI.event.repository.PersonalEventRepository;
import com.JAI.todo.repository.PersonalTodoRepository;
import com.JAI.alarm.repository.AlarmRepository;
import com.JAI.config.JacksonConfig;
import com.JAI.global.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({ApprovalPersistenceService.class, ApprovalOrchestrator.class, ApprovalRedisRepository.class,
        RedisChatbotUtil.class, RedisUtil.class, JacksonConfig.class, ApprovalIntegrationTest.RedisTestConfig.class})
@Testcontainers
class ApprovalIntegrationTest {
    @Container static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.40");
    @Container static final GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        r.add("spring.datasource.hikari.maximum-pool-size", () -> "20");
    }
    @org.springframework.boot.test.context.TestConfiguration static class RedisTestConfig {
        @Bean LettuceConnectionFactory testRedisConnectionFactory() {
            return new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        }
        @Bean RedisTemplate<String,String> redisTemplate(LettuceConnectionFactory f) {
            RedisTemplate<String,String> t = new RedisTemplate<>();
            t.setConnectionFactory(f); t.setDefaultSerializer(new StringRedisSerializer()); return t;
        }
    }
    @Autowired jakarta.persistence.EntityManager entityManager;
    @Autowired ApprovalOrchestrator orchestrator;
    @Autowired ApprovalPersistenceService persistence;
    @SpyBean ApprovalRedisRepository claims;
    @Autowired RedisChatbotUtil snapshots;
    @Autowired RedisTemplate<String,String> redisTemplate;
    @Autowired ApprovalLedgerRepository ledgers;
    @Autowired ApprovalItemRepository items;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;
    @SpyBean PersonalEventRepository events;
    @SpyBean PersonalTodoRepository todos;
    @SpyBean AlarmRepository alarms;
    UUID userId, categoryId;

    @BeforeEach void setup() {
        items.deleteAll(); ledgers.deleteAll(); alarms.deleteAll(); events.deleteAll(); todos.deleteAll();
        categories.deleteAll(); users.deleteAll();
        User user = users.saveAndFlush(User.create("tester", "tester@example.org", "unused", Provider.ORIGIN));
        userId = user.getUserId();
        categoryId = categories.saveAndFlush(Category.create("personal", "blue", user, null)).getCategoryId();
    }
    String proposal(String type) {
        String token = UUID.randomUUID().toString();
        snapshots.saveChatbotData(UUID.randomUUID().toString(), token, userId,
                ChatbotReqDTO.builder().categoryId(categoryId).intention(type).prompt("test").build());
        if (type.equals("EVENT")) snapshots.saveChatbotEventResp(token, List.of(event("one"), event("two")));
        else snapshots.saveChatbotTodoResp(token, List.of(todo("one"), todo("two")));
        return token;
    }
    ChatbotEventRespDTO event(String title) {
        return ChatbotEventRespDTO.builder().title(title).startDate(LocalDate.of(2030,1,1))
                .endDate(LocalDate.of(2030,1,1)).startTime(LocalTime.NOON).endTime(LocalTime.of(13,0)).build();
    }
    ChatbotTodoRespDTO todo(String title) {
        return ChatbotTodoRespDTO.builder().title(title).date(LocalDate.of(2030,1,1)).build();
    }
    @ParameterizedTest @org.junit.jupiter.params.provider.CsvSource({"1,EVENT","10,EVENT","50,EVENT","100,EVENT","1,TODO","10,TODO","50,TODO","100,TODO"})
    void concurrentApprovalsCommitOnlyOneBatch(int count, String type) throws Exception {
        String token = proposal(type);
        List<ApprovalResult> results = concurrent(count, () -> orchestrator.process(userId, token, true, true));
        ApprovalResult replay = orchestrator.process(userId, token, true, true);
        assertTrue(replay.replayed());
        assertEquals(1, ledgers.count()); assertEquals(type.equals("EVENT")?2:0, events.count());
        assertEquals(type.equals("TODO")?2:0, todos.count());
        assertEquals(type.equals("EVENT")?2:0, alarms.count()); assertEquals(2, items.count());
        results.stream().filter(r -> r.status().equals("COMPLETED")).forEach(r -> assertEquals(replay.items(), r.items()));
    }
    @Test void todoReplayAndDifferentTokenWithIdenticalContent() {
        String token = proposal("TODO");
        ApprovalResult first = orchestrator.process(userId, token, true, false);
        assertEquals(first.items(), orchestrator.process(userId, token, true, false).items());
        orchestrator.process(userId, proposal("TODO"), true, false);
        assertEquals(4, todos.count()); assertEquals(2, ledgers.count()); assertEquals(0, alarms.count());
    }
    @ParameterizedTest @ValueSource(strings={"EVENT","TODO","ALARM"})
    void secondWriteFailureRollsBackWholeBatchAndAllowsRetry(String target) {
        String token = proposal(target.equals("TODO") ? "TODO" : "EVENT");
        AtomicInteger writes = new AtomicInteger();
        org.mockito.stubbing.Answer<Object> failSecond = invocation -> {
            Object saved = invocation.getArgument(0);
            entityManager.persist(saved);
            entityManager.flush();
            if (writes.incrementAndGet() == 2) throw new IllegalStateException("injected second write failure");
            return saved;
        };
        if(target.equals("EVENT")) doAnswer(failSecond).when(events).save(any());
        else if(target.equals("TODO")) doAnswer(failSecond).when(todos).save(any());
        else doAnswer(failSecond).when(alarms).save(any());
        assertThrows(IllegalStateException.class, () -> orchestrator.process(userId, token, true, true));
        assertEquals(0, ledgers.count()); assertEquals(0, items.count()); assertEquals(0, events.count());
        assertEquals(0, todos.count()); assertEquals(0, alarms.count());
        reset(events, todos, alarms);
        assertEquals("COMPLETED", orchestrator.process(userId, token, true, true).status());
    }
    @Test void cleanupFailureAndLostResponseReplayDurableIds() {
        String token = proposal("EVENT");
        doThrow(new IllegalStateException("Redis unavailable")).when(claims).cleanupAfterCommit(eq(token), any());
        ApprovalResult first = orchestrator.process(userId, token, true, true);
        redisTemplate.delete(ApprovalRedisRepository.proposalKey(token));
        doThrow(new IllegalStateException("Redis still unavailable")).when(claims).claim(eq(token), any(), any());
        assertEquals(first.items(), orchestrator.process(userId, token, true, true).items());
        assertEquals(2, events.count());
    }
    @Test void decisionsAndAlarmChangesConflictAndOtherUserCannotClaim() {
        String token = proposal("EVENT");
        assertStatus(403, () -> orchestrator.process(UUID.randomUUID(), token, true, false));
        orchestrator.process(userId, token, true, false);
        assertStatus(409, () -> orchestrator.process(userId, token, true, true));
        assertStatus(409, () -> orchestrator.process(userId, token, false, false));
        String rejected = proposal("TODO");
        orchestrator.process(userId, rejected, false, false);
        assertTrue(orchestrator.process(userId, rejected, false, true).replayed());
        assertStatus(409, () -> orchestrator.process(userId, rejected, true, false));
    }
    @Test void expiredLeaseCannotReleaseNewOwnerAndProposalSurvives() throws Exception {
        String token = proposal("TODO"); UUID old = UUID.randomUUID(), next = UUID.randomUUID();
        assertEquals(ApprovalRedisRepository.Status.ACQUIRED, claims.claim(token,userId,old).status());
        assertEquals(ApprovalRedisRepository.Status.BUSY, claims.claim(token,userId,next).status());
        redisTemplate.expire("approval:{"+token+"}:lease", Duration.ofMillis(1));
        Thread.sleep(20);
        assertEquals(ApprovalRedisRepository.Status.ACQUIRED, claims.claim(token,userId,next).status());
        claims.release(token,old); assertFalse(claims.cleanupAfterCommit(token,old));
        assertEquals(ApprovalRedisRepository.Status.BUSY, claims.claim(token,userId,old).status());
        claims.release(token,next);
        assertEquals("COMPLETED", orchestrator.process(userId,token,true,false).status());
        assertStatus(410, () -> orchestrator.process(userId, UUID.randomUUID().toString(), true, false));
    }
    @Test void uniqueConstraintProtectsEvenWithoutRedisLease() throws Exception {
        String token = proposal("EVENT"); ChatbotRedisDataDTO p = snapshots.getChatbotData(token);
        AtomicInteger committed = new AtomicInteger();
        concurrent(50, () -> {
            try { persistence.persist(new ApprovalCommand(userId,token,true,true,"request","proposal"),p); committed.incrementAndGet(); }
            catch(org.springframework.dao.DataIntegrityViolationException expected) { }
            return null;
        });
        assertEquals(1, committed.get()); assertEquals(1, ledgers.count());
        assertEquals(2, events.count()); assertEquals(2, alarms.count());
    }
    @Test void acceptRejectRaceCommitsOnlyWinningDecision() throws Exception {
        String token = proposal("EVENT"); AtomicInteger sequence = new AtomicInteger();
        concurrent(50, () -> {
            try { return orchestrator.process(userId,token,sequence.getAndIncrement()%2==0,false); }
            catch(ResponseStatusException e) { assertEquals(409,e.getStatusCode().value()); return null; }
        });
        assertEquals(1,ledgers.count());
        assertEquals(ledgers.findAll().get(0).getDecision().equals("ACCEPTED") ? 2 : 0,events.count());
    }
    @Test void invalidSecondItemAndForeignCategoryLeaveNoRows() {
        String token = proposal("EVENT");
        ChatbotRedisDataDTO original = snapshots.getChatbotData(token);
        ChatbotRedisDataDTO invalid = ChatbotRedisDataDTO.builder().token(token).userId(userId)
                .chatbotReqDTO(original.getChatbotReqDTO())
                .chatbotEventRespDTO(List.of(event("valid"),event(" "))).build();
        assertStatus(400, () -> persistence.persist(new ApprovalCommand(userId,token,true,true,"r","p"),invalid));
        assertEquals(0,events.count()); assertEquals(0,ledgers.count());
        User other = users.saveAndFlush(User.create("other","other@example.org","unused",Provider.ORIGIN));
        Category foreign = categories.saveAndFlush(Category.create("foreign","red",other,null));
        ChatbotRedisDataDTO forbidden = ChatbotRedisDataDTO.builder().token(token).userId(userId)
                .chatbotReqDTO(ChatbotReqDTO.builder().categoryId(foreign.getCategoryId()).intention("EVENT").build())
                .chatbotEventRespDTO(List.of(event("valid"))).build();
        assertStatus(403, () -> persistence.persist(new ApprovalCommand(userId,token,true,true,"r","p"),forbidden));
        assertEquals(0,ledgers.count());
    }
    @Test void readySnapshotCannotBeOverwritten() {
        String token=proposal("TODO");
        String before=redisTemplate.opsForValue().get(ApprovalRedisRepository.proposalKey(token));
        assertThrows(com.JAI.chatbot.exception.ChatbotUnprocessableEntityException.class,
                () -> snapshots.saveChatbotTodoResp(token,List.of(todo("changed"))));
        assertEquals(before,redisTemplate.opsForValue().get(ApprovalRedisRepository.proposalKey(token)));
    }
    @Autowired javax.sql.DataSource dataSource;
    @Test void productionMigrationCreatesUsableLedgerConstraints() {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(dataSource);
        jdbc.execute("DROP TABLE chatbot_approval_item");
        jdbc.execute("DROP TABLE chatbot_approval_ledger");
        new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(
                new org.springframework.core.io.FileSystemResource("sql/20261023_chatbot_approval_ledger.sql"))
                .execute(dataSource);
        String token=proposal("EVENT");
        orchestrator.process(userId,token,true,true);
        assertTrue(orchestrator.process(userId,token,true,true).replayed());
        assertEquals(1,ledgers.count());
    }
    void assertStatus(int status, org.junit.jupiter.api.function.Executable action) {
        assertEquals(status, assertThrows(ResponseStatusException.class, action).getStatusCode().value());
    }
    <T> List<T> concurrent(int n, Callable<T> action) throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(n); CountDownLatch start=new CountDownLatch(1);
        try {
            List<Future<T>> futures=new ArrayList<>();
            for(int i=0;i<n;i++) futures.add(pool.submit(() -> {start.await(); return action.call();}));
            start.countDown(); List<T> values=new ArrayList<>();
            for(Future<T> f:futures) values.add(f.get(60,TimeUnit.SECONDS));
            return values;
        } finally {pool.shutdownNow();}
    }
}
