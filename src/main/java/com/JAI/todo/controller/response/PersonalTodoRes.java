package com.JAI.todo.controller.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PersonalTodoRes {
    UUID personalTodoId;

    String title;

    boolean done;

    int todoOrder;

    LocalDate date;

    LocalTime startTime;

    LocalDateTime createdAt;

    UUID userId;

    UUID categoryId;
}
