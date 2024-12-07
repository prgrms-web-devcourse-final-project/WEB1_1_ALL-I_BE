package com.JAI.todo.controller.request;

import com.JAI.todo.controller.response.GroupMemberStateRes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "내용은 필수 입니다.")
    String title;
    @NotNull(message = "날짜는 필수 입니다.")
    LocalDate date;

    LocalTime startTime;

    List<UUID> userIdList;
}
