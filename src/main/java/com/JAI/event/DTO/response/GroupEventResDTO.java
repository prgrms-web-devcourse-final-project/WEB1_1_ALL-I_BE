package com.JAI.event.DTO.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class GroupEventResDTO {
    UUID groupEventId;

    String title;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    LocalDateTime createdAt;

    UUID groupId;

    UUID categoryId;

    List<UUID> assignedUserIds;

    public void updateUserIds(List<UUID> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }
}
