package com.JAI.event.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class GroupEventCreateReqDTO {
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

    @NotNull(message = "group Id must not be null")
    UUID groupId;

    @NotNull(message = "assigned member list must not be null")
    List<UUID> assignedMemberList;
}
