package com.JAI.chatbot.approval.service;

import com.JAI.alarm.domain.Alarm;
import com.JAI.alarm.domain.AlarmType;
import com.JAI.alarm.repository.AlarmRepository;
import com.JAI.category.domain.Category;
import com.JAI.category.repository.CategoryRepository;
import com.JAI.chatbot.approval.domain.ApprovalItem;
import com.JAI.chatbot.approval.domain.ApprovalLedger;
import com.JAI.chatbot.approval.dto.ApprovalCommand;
import com.JAI.chatbot.approval.dto.ApprovalResult;
import com.JAI.chatbot.approval.repository.ApprovalItemRepository;
import com.JAI.chatbot.approval.repository.ApprovalLedgerRepository;
import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotEventRespDTO;
import com.JAI.chatbot.controller.dto.response.ChatbotTodoRespDTO;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.event.repository.PersonalEventRepository;
import com.JAI.todo.domain.PersonalTodo;
import com.JAI.todo.repository.PersonalTodoRepository;
import com.JAI.user.domain.User;
import com.JAI.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The only durable write boundary for chatbot approval.
 * The Ledger INSERT and all resulting rows commit or roll back together.
 */
@Service
@RequiredArgsConstructor
public class ApprovalPersistenceService {
    private final ApprovalLedgerRepository ledgerRepository;
    private final ApprovalItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PersonalEventRepository eventRepository;
    private final PersonalTodoRepository todoRepository;
    private final AlarmRepository alarmRepository;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApprovalResult persist(ApprovalCommand command, ChatbotRedisDataDTO proposal) {
        if (proposal == null || !command.userId().equals(proposal.getUserId())
                || !command.token().equals(proposal.getToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "추천안 소유자가 일치하지 않습니다");
        }
        // The unique key coordinates competing requests, even after the Redis lease expires.
        ApprovalLedger ledger = ledgerRepository.saveAndFlush(ApprovalLedger.processing(
                command.userId(), command.token(), command.requestHash(),
                command.proposalHash(), command.accept()));

        ApprovalResult result;
        if (!command.accept()) {
            result = ApprovalResult.rejected();
        } else {
            result = createAll(command, proposal, ledger);
        }
        ledger.complete(serialize(result));
        ledgerRepository.save(ledger);
        // JPA commits the Ledger, Event/Todo, Alarm and Item rows as one unit.
        return result;
    }

    private ApprovalResult createAll(ApprovalCommand command, ChatbotRedisDataDTO proposal,
                                     ApprovalLedger ledger) {
        if (proposal.getChatbotReqDTO() == null || proposal.getChatbotReqDTO().getCategoryId() == null) {
            throw badRequest("카테고리를 선택해 주세요");
        }
        Category category = categoryRepository.findById(proposal.getChatbotReqDTO().getCategoryId())
                .orElseThrow(() -> badRequest("카테고리가 존재하지 않습니다"));
        if (category.getUser() == null || !command.userId().equals(category.getUser().getUserId())
                || category.getGroup() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 개인 카테고리만 사용할 수 있습니다");
        }
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다"));
        String intent = proposal.getChatbotReqDTO().getIntention();
        List<ApprovalResult.ItemResult> created = new ArrayList<>();

        if ("EVENT".equals(intent)) {
            List<ChatbotEventRespDTO> events = proposal.getChatbotEventRespDTO();
            validateEvents(events);  // Validate ALL before writing ANY item.
            for (int i = 0; i < events.size(); i++) {
                ChatbotEventRespDTO e = events.get(i);
                PersonalEvent event = PersonalEvent.builder()
                        .title(e.getTitle().trim())
                        .startDate(e.getStartDate()).startTime(e.getStartTime())
                        .endDate(e.getEndDate()).endTime(e.getEndTime())
                        .isAlarmed(command.alarm()).user(user).category(category)
                        .build();
                eventRepository.save(event);
                if (command.alarm()) {
                    ZonedDateTime schedule = e.getStartDate()
                            .atTime(e.getStartTime() == null ? LocalTime.now(ZoneId.of("Asia/Seoul")) : e.getStartTime())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .withZoneSameInstant(ZoneId.of("UTC"));
                    alarmRepository.save(Alarm.builder()
                            .type(AlarmType.EVENT)
                            .scheduledTime(schedule)
                            .description("일정: " + e.getTitle().substring(0, Math.min(e.getTitle().length(), 790)))
                            .user(user).personalEvent(event).build());
                }
                recordItem(command.token(), ledger, i, "EVENT", event.getPersonalEventId(), created);
            }
        } else if ("TODO".equals(intent) || "PLAN_RECOMMENDATION".equals(intent)) {
            List<ChatbotTodoRespDTO> todos = proposal.getChatbotTodoRespDTO();
            validateTodos(todos);
            for (int i = 0; i < todos.size(); i++) {
                ChatbotTodoRespDTO t = todos.get(i);
                PersonalTodo todo = PersonalTodo.create(t.getTitle().trim(), t.getDate(),
                        t.getTime(), user, category);
                todoRepository.save(todo);
                recordItem(command.token(), ledger, i, "TODO", todo.getPersonalTodoId(), created);
            }
        } else {
            throw badRequest("지원하지 않는 추천 유형입니다");
        }
        return ApprovalResult.completed(created);
    }

    private void recordItem(String token, ApprovalLedger ledger, int index, String type, UUID id,
                            List<ApprovalResult.ItemResult> results) {
        UUID itemId = com.JAI.chatbot.approval.dto.ProposalIdentity.itemId(token, type, index);
        itemRepository.save(ApprovalItem.of(ledger, itemId, type, id));
        results.add(new ApprovalResult.ItemResult(itemId, type, id));
    }

    private static void validateEvents(List<ChatbotEventRespDTO> events) {
        if (events == null || events.isEmpty()) throw badRequest("등록할 일정이 없습니다");
        for (ChatbotEventRespDTO event : events) {
            if (event == null || event.getTitle() == null || event.getTitle().isBlank()
                    || event.getTitle().length() > 255
                    || event.getStartDate() == null || event.getEndDate() == null
                    || event.getStartDate().isAfter(event.getEndDate())
                    || (event.getStartDate().equals(event.getEndDate())
                        && event.getStartTime() != null && event.getEndTime() != null
                        && event.getStartTime().isAfter(event.getEndTime()))) {
                throw badRequest("일정의 제목 또는 날짜/시간 형식이 잘못되었습니다");
            }
        }
    }

    private static void validateTodos(List<ChatbotTodoRespDTO> todos) {
        if (todos == null || todos.isEmpty()) throw badRequest("등록할 할 일이 없습니다");
        for (ChatbotTodoRespDTO todo : todos) {
            if (todo == null || todo.getTitle() == null || todo.getTitle().isBlank()
                    || todo.getTitle().length() > 255 || todo.getDate() == null) {
                throw badRequest("할 일의 제목 또는 날짜가 잘못되었습니다");
            }
        }
    }

    private String serialize(ApprovalResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("승인 결과 직렬화에 실패했습니다", e);
        }
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
