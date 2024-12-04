package com.JAI.event.DTO;

import com.JAI.category.domain.Category;
import com.JAI.user.domain.User;
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
public class PersonalEventDTO {
    UUID personalEventId;

    String title;

    LocalDate startDate;

    LocalDate endDate;

    LocalTime startTime;

    LocalTime endTime;

    Boolean isAlarmed;

    LocalDateTime createdAt;

    Category category;

    User user;
}
