package com.JAI.todo.service;

import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.request.PersonalTodoStateReq;
import com.JAI.todo.controller.request.PersonalTodoUpdateReq;
import com.JAI.todo.controller.request.PersonalTodoUpdateTitleReq;
import com.JAI.todo.controller.response.*;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface PersonalTodoService {
    void createPersonalTodo(PersonalTodoCreateReq req, CustomUserDetails user);
    List<PersonalTodoRes> getMonthlyPersonalTodoList(String year, String month, CustomUserDetails user);
    List<PersonalTodoExistListRes> getPersonalTodosExist(String year, String month, CustomUserDetails user);

    List<PersonalTodoRes> getDailyPersonalTodoList(String year, String month, String day, CustomUserDetails userDetails);
    void deletePersonalTodo(UUID todoId, CustomUserDetails user);

    PersonalTodoUpdateRes updatePersonalTodo(UUID todoId, PersonalTodoUpdateReq req, CustomUserDetails user);
    PersonalTodoStateRes updatePersonTodoState(UUID todoId, PersonalTodoStateReq req, CustomUserDetails user);
    PersonalTodoUpdateTitleRes updatePersonalTodoTitle(UUID todoId, PersonalTodoUpdateTitleReq req, CustomUserDetails user);
}
