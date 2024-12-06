package com.JAI.todo.converter;

import com.JAI.group.domain.GroupSetting;
import com.JAI.todo.controller.response.GroupMemberStateRes;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.domain.GroupTodoMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupTodoMappingConverter {
    //DTO -> Entity
    public GroupTodoMapping toGroupTodoMappingEntity(GroupTodo groupTodo, GroupSetting groupSetting){
        return GroupTodoMapping.create(
                groupTodo,
                groupSetting
        );
    }

    public GroupMemberStateRes toGroupMemberStateDTO(UUID userId, boolean done){
        return GroupMemberStateRes.builder()
                .userId(userId)
                .done(done)
                .build();
    }
}
