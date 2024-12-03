package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmNotificationService {
    private final Map<UUID, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final AlarmService alarmService;

    // 실시간 알림 조회
    public SseEmitter getRealTimeAlarms(UUID userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userEmitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            userEmitters.remove(userId);
            log.info("SSE connection completed for userId: {}", userId);
        });
        emitter.onTimeout(() -> {
            userEmitters.remove(userId);
            log.warn("SSE connection timed out for userId: {}", userId);
        });
        emitter.onError(throwable -> {
            userEmitters.remove(userId);
            log.error("SSE connection error for userId: {}", userId, throwable);
        });

        log.info("SSE connection established for userId: {}", userId);
        return emitter;
    }

    // 실시간 알림 전송
    @Scheduled(fixedRate = 60000)
    public void sendScheduledAlarms() {
        log.info("Checking scheduled alarms...");
        LocalDateTime start = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(1);
        LocalDateTime end = LocalDateTime.now();

        // 현재 시간에 도달한 알림을 조회
        alarmService.findPendingAlarms(start, end).forEach(alarmResDTO -> {

            // 사용자 연결 여부 확인 후 전송
            if (userEmitters.containsKey(alarmResDTO.getUserId())) {
                sendToUser(alarmResDTO.getUserId(), alarmResDTO);
                alarmService.markAlarmAsSent(alarmResDTO);

                log.info("Alarm sent: {}", alarmResDTO);

            } else {
                log.warn("User {} has no active SSE connection, alarm not sent.", alarmResDTO.getUserId());
            }
        });
    }

    // 실시간 알림 조회를 요청한 사용자에게 알림 전송
    private void sendToUser(UUID userId, AlarmResDTO alarmResDTO) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                emitter.send(alarmResDTO);
            } catch (IOException e) {
                log.error("Error sending alarm to user: {}", userId, e);
            }
        } else {
            log.warn("No active SSE connection found for user: {}", userId);
        }
    }
}
