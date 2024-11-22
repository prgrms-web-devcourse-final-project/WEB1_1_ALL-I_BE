package com.JAI.event.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PersonalCreateEventReqDTO {
    String description;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    UUID userId;

    UUID categoryId;
}
