package com.JAI.todo.service;

import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.request.GroupTodoUpdateReq;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.controller.response.GroupTodoInfoRes;
import com.JAI.todo.controller.response.GroupTodoRes;
import com.JAI.todo.controller.response.GroupTodoUpdateRes;


import java.util.UUID;

public interface GroupTodoService {

    GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId, UUID userId);

    GroupTodoInfoRes getGroupTodos(UUID groupId, UUID userId, String year, String month);

    GroupTodoInfoRes getMyGroupTodos(UUID userId, String year, String month);

    GroupTodoInfoRes getGroupMemberGroupTodos(UUID groupId, UUID groupMemberId, UUID userId, String year, String month);

    GroupTodoRes updateGroupTodoState(UUID groupTodoId);

    GroupTodoUpdateRes updateGroupTodoInfo(GroupTodoUpdateReq req, UUID groupTodoId, UUID groupId, UUID user);

    void deleteGroupTodo(UUID groupTodoId, UUID groupId, UUID userId);

}
