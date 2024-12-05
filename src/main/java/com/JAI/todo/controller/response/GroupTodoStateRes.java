package com.JAI.todo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupTodoStateRes {
    GroupTodoRes groupTodoRes;
    GroupMemberStateRes groupMemberStateRes;
}
