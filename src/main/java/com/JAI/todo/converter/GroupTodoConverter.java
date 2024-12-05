package com.JAI.todo.converter;

import com.JAI.group.domain.Group;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.request.GroupTodoUpdateReq;
import com.JAI.todo.controller.response.GroupTodoRes;
import com.JAI.todo.controller.response.GroupTodoUpdateRes;
import com.JAI.todo.domain.GroupTodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    // Entity -> DTO
    public GroupTodoRes toGroupTodoResDTO(GroupTodo groupTodo){
        return GroupTodoRes.builder()
                .groupTodoId(groupTodo.getGroupTodoId())
                .title(groupTodo.getTitle())
                .done(groupTodo.isDone())
                .todoOrder(groupTodo.getTodoOrder())
                .date(groupTodo.getDate())
                .startTime(groupTodo.getStartTime())
                .createdAt(groupTodo.getCreatedAt())
                .groupId(groupTodo.getGroup().getGroupId())
                .build();
    }

    public GroupTodoUpdateRes toGroupTodoUpdateDTO(GroupTodo groupTodo, List<UUID> userIdList){
        return GroupTodoUpdateRes.builder()
                .groupTodoId(groupTodo.getGroupTodoId())
                .title(groupTodo.getTitle())
                .done(groupTodo.isDone())
                .todoOrder(groupTodo.getTodoOrder())
                .date(groupTodo.getDate())
                .startTime(groupTodo.getStartTime())
                .createdAt(groupTodo.getCreatedAt())
                .groupId(groupTodo.getGroup().getGroupId())
                .userIdList(userIdList)
                .build();
    }

}
