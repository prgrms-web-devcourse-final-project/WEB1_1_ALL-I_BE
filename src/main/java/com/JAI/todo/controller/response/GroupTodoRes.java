package com.JAI.todo.controller.response;

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
@Data
public class GroupTodoRes {

    UUID groupTodoId;

    String title;

    boolean done;

    int todoOrder;

    LocalDate date;

    LocalTime startTime;

    LocalDateTime createdAt;

    List<UUID> userIdList;

    public void updateUserIds(List<UUID> userIdList) {
        this.userIdList = userIdList;
    }

}
