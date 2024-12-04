package com.JAI.todo.service;

import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.AllGroupTodoRes;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.controller.response.MyGroupTodosRes;

import java.util.UUID;

public interface GroupTodoService {
    GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId);
    AllGroupTodoRes getGroupTodos(UUID groupId, UUID userId, String year, String month);
    MyGroupTodosRes getMyGroupTodos(UUID userId, String year, String month);
}
