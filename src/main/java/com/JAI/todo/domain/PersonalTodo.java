package com.JAI.todo.domain;

import com.JAI.category.domain.Category;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)// 객체 생성 시 JPA에서 생성 시간 자동 기입
@Table(name = "personal_todo")
@Entity
public class PersonalTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="personal_todo_id", columnDefinition = "BINARY(16)")
    private UUID personalTodoId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean done;

    @Column(nullable = false)
    private int todo_order;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = true)
    private LocalTime startTime;

    // 객체 생성 시 JPA에서 생성 시간 자동 기입
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Builder
    private PersonalTodo(String description, boolean done, int todo_order, LocalDate date, LocalTime startTime, User user, Category category){
        this.description = description;
        this.done = done;
        this.todo_order = todo_order;
        this.date = date;
        this.startTime = startTime;
        this.user = user;
        this.category = category;
    }

    public static PersonalTodo create(String description, LocalDate date, LocalTime startTime, User user, Category category){
        return PersonalTodo.builder()
                .description(description)
                .done(false)    //default
                .todo_order(0)   //default
                .date(date)
                .startTime(startTime)
                .user(user)
                .category(category)
                .build();
    }
}
