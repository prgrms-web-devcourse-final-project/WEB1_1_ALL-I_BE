package com.JAI.group.domain;

import com.JAI.category.domain.Category;
import com.JAI.event.domain.GroupEvent;
import com.JAI.todo.domain.GroupTodo;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "name", nullable = false, length = 155)
    private String name;

    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @OneToMany(mappedBy = "group_setting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupSetting> groupSettings;

    @OneToMany(mappedBy = "group_event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEvent> groupEvents;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "group_todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTodo> groupTodos;

    @Builder
    private Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Group create(String name, String description) {
        return Group.builder()
                .name(name)
                .description(description)
                .build();
    }

}
