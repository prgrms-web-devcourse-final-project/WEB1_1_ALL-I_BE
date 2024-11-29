package com.JAI.todo.service;

import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.request.PersonalTodoStateReq;
import com.JAI.todo.controller.response.PersonalTodoListRes;
import com.JAI.todo.controller.response.PersonalTodoStateRes;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface PersonalTodoService {
    void createPersonalTodo(PersonalTodoCreateReq req, CustomUserDetails user);
    List<PersonalTodoListRes> getTodoListForMonth(String year, String month, CustomUserDetails user);
    void deletePersonalTodo(UUID todoId, CustomUserDetails user);

    PersonalTodoStateRes updatePersonTodoState(UUID todoId, PersonalTodoStateReq req, CustomUserDetails userDetails);
}
