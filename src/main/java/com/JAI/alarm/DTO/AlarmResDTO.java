package com.JAI.alarm.DTO;

import com.JAI.alarm.domain.AlarmType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class AlarmResDTO {
    private UUID alarmId;

    private AlarmType type;

    private String description;

    private UUID userId;

    @Builder
    private AlarmResDTO(UUID alarmId, AlarmType type, String description, UUID userId) {
        this.type = type;
        this.description = description;
        this.alarmId = alarmId;
        this.userId = userId;
    }
}
