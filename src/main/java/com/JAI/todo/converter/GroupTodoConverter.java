package com.JAI.todo.converter;

import com.JAI.group.domain.Group;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.domain.GroupTodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupTodoConverter {
    //DTO -> Entity
    public GroupTodo toGroupTodoEntity(GroupTodoCreateReq req, Group group){
        return GroupTodo.create(
                req.getTitle(),
                req.getDate(),
                req.getStartTime(),
                group
        );
    }
}
