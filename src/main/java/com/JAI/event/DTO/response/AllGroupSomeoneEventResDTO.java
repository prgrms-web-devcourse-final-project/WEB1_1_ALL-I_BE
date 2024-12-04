package com.JAI.event.DTO.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class AllGroupSomeoneEventResDTO {
    UUID groupEventId;

    String title;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    LocalDateTime createdAt;

    UUID groupId;
}
