package com.JAI.todo.service;

import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.GroupTodoCreateRes;

import java.util.UUID;

public interface GroupTodoService {
    GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId);
}
