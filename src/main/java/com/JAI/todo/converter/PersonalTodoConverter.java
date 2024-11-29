package com.JAI.todo.converter;

import com.JAI.category.domain.Category;
import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.response.PersonalTodoListRes;
import com.JAI.todo.controller.response.PersonalTodoStateRes;
import com.JAI.todo.controller.response.PersonalTodoUpdateRes;
import com.JAI.todo.domain.PersonalTodo;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// TODO :: todo 작업
@Component
@RequiredArgsConstructor
public class PersonalTodoConverter {
    //DTO -> Entity
    public PersonalTodo toPersonalTodoCreateEntity(PersonalTodoCreateReq request, User user, Category category){
        PersonalTodo personalTodo = PersonalTodo.create(
                request.getTitle(),
                request.getDate(),
                request.getStartTime(),
                user,
                category
        );
        return personalTodo;
    }

    //Entity -> DTO
    public PersonalTodoListRes toPersonalTodoListDTO(PersonalTodo personalTodo){
        return PersonalTodoListRes.builder()
                .personalTodoId(personalTodo.getPersonalTodoId())
                .title(personalTodo.getTitle())
                .done(personalTodo.isDone())
                .todoOrder(personalTodo.getTodoOrder())
                .date(personalTodo.getDate())
                .startTime(personalTodo.getStartTime())
                .createdAt(personalTodo.getCreatedAt())
                .userId(personalTodo.getUser().getUserId())
                .categoryId(personalTodo.getCategory().getCategoryId())
                .build();
    }

    public PersonalTodoStateRes toPersonalTodoStateDTO(PersonalTodo personalTodo){
        return PersonalTodoStateRes.builder()
                .todoId(personalTodo.getPersonalTodoId())
                .done(personalTodo.isDone())
                .build();
    }

    public PersonalTodoUpdateRes toPersonalTodoUpdateDTO(PersonalTodo personalTodo){
        return PersonalTodoUpdateRes.builder()
                .todoId(personalTodo.getPersonalTodoId())
                .title(personalTodo.getTitle())
                .categoryId(personalTodo.getCategory().getCategoryId())
                .startTime(personalTodo.getStartTime())
                .date(personalTodo.getDate())
                .build();
    }
}
