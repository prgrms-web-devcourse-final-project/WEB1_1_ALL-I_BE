package com.JAI.todo.domain;

import com.JAI.group.domain.GroupSetting;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_todo_mapping")
@Entity
public class GroupTodoMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="group_todo_mapping_id", columnDefinition = "BINARY(16)")
    private UUID groupTodoMappingId;

    private boolean done;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_todo_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupTodo groupTodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_setting_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupSetting groupSetting;

    @Builder
    private GroupTodoMapping(boolean done, GroupTodo groupTodo, GroupSetting groupSetting){
        this.done = done;
        this.groupTodo = groupTodo;
        this.groupSetting = groupSetting;
    }

    public static GroupTodoMapping create(GroupTodo groupTodo, GroupSetting groupSetting){
        return GroupTodoMapping.builder()
                .done(false)    //default
                .groupTodo(groupTodo)
                .groupSetting(groupSetting)
                .build();
    }

    public void updateGroupTodoMappingState(boolean done){
        if(this.done != done){
            this.done = done;
        }
    }
}
