package com.JAI.todo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupTodoUpdateRes {
    UUID groupTodoId;

    String title;

    boolean done;

    int todoOrder;

    LocalDate date;

    LocalTime startTime;

    LocalDateTime createdAt;

    UUID groupId;

    List<UUID> userIdList;
}
