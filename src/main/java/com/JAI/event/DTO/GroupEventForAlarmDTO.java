package com.JAI.event.DTO;

import com.JAI.group.domain.Group;
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
public class GroupEventForAlarmDTO {
    UUID groupEventId;

    String title;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    LocalDateTime createdAt;

    Group group;

    UUID categoryId;

    List<UUID> assignedUserIds;

    public void updateUserIds(List<UUID> assignedUserIds) {
        if (this.assignedUserIds == null) {
            this.assignedUserIds = assignedUserIds;
        }
    }
}
