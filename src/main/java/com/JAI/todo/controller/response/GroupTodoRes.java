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

    UUID groupId;

    UUID categoryId;

    List<GroupMemberStateRes> userIdList;

    public void updateCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public void updateUserIdList(List<GroupMemberStateRes> userIdList) {
        this.userIdList = userIdList;
    }

    public void toGroupMappingDone(boolean done) {
        this.done = done;
    }

}
