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
@Data
public class PersonalEventCreateReqDTO {
    @NotNull(message = "title must not be null")
    String title;

    @NotNull(message = "start date must not be null")
    LocalDate startDate;

    @NotNull(message = "end date must not be null")
    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    @NotNull(message = "is alarmed must not be null")
    Boolean isAlarmed;

    @NotNull(message = "Category Id must not be null")
    UUID categoryId;
}
