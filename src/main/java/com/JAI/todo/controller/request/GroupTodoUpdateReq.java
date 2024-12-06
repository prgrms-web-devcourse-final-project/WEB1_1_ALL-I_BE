package com.JAI.todo.controller.request;

import com.JAI.todo.controller.response.GroupMemberStateRes;
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
public class GroupTodoUpdateReq {

    String title;

    LocalDate date;

    LocalTime startTime;

    List<GroupMemberStateRes> userIdList;
}
