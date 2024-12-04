package com.JAI.todo.converter;

import com.JAI.group.domain.GroupSetting;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.domain.GroupTodoMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
