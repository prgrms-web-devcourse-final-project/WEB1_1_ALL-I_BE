package com.JAI.event.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class PersonalEventUpdateReqDTO {
    @NotNull(message = "Personal Event ID must not be null")
    UUID personalEventId;

    String title;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    UUID categoryId;
}
