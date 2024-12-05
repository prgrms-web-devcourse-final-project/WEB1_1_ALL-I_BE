package com.JAI.todo.service;

import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.controller.response.GroupTodoInfoRes;


import java.util.UUID;

public interface GroupTodoService {

    GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId);

    GroupTodoInfoRes getGroupTodos(UUID groupId, UUID userId, String year, String month);

    GroupTodoInfoRes getMyGroupTodos(UUID userId, String year, String month);

    GroupTodoInfoRes getGroupMemberGroupTodos(UUID groupId, UUID groupMemberId, UUID userId, String year, String month);

    void updateGroupTodoState(UUID groupTodoId);
}
