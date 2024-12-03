package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.event.DTO.PersonalEventDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlarmService {
    public void createAlarm(PersonalEventDTO personalEventDTO);

    public void updateAlarm(PersonalEventDTO personalEventDTO);

    private void deleteAlarm(UUID userId) {

    }

    public void getAlarm(UUID alarmId, SseEmitter emitter);

    public List<AlarmResDTO> findPendingAlarms(LocalDateTime time);

    public void markAlarmAsSent(AlarmResDTO alarmResDTO);
}
