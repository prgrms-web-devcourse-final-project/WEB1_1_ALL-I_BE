package com.JAI.category.domain;

import com.JAI.event.domain.PersonalEvent;
import com.JAI.group.domain.Group;
import com.JAI.todo.domain.PersonalTodo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.util.List;

import com.JAI.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", columnDefinition = "BINARY(16)")
    private UUID categoryId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalTodo> personalTodos;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalEvent> personalEvents;

    @OneToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @Builder
    private Category(String name, String color, User user, Group group) {
        this.name = name;
        this.color = color;
        this.user = user;
        this.group = group;
    }

    private static Category create(String name, String color, User user, Group group) {
        return Category.builder()
                .name(name)
                .color(color)
                .user(user)
                .group(group)
                .build();
    }

}
