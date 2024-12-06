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

    private Boolean isRead;

    private UUID userId;

    @Builder
    private AlarmResDTO(UUID alarmId, AlarmType type, String description, Boolean isRead, UUID userId) {
        this.alarmId = alarmId;
        this.type = type;
        this.description = description;
        this.isRead = isRead;
        this.userId = userId;
    }
}
