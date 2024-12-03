package com.JAI.alarm.mapper;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.alarm.domain.Alarm;
import org.springframework.stereotype.Component;

@Component
public class AlarmConverter {
    public AlarmResDTO alarmResDTOToAlarm(Alarm alarm) {
        return AlarmResDTO.builder()
                .alarmId(alarm.getAlarmId())
                .type(alarm.getType())
                .description(alarm.getDescription())
                .userId(alarm.getUser().getUserId())
                .build();
    }
}