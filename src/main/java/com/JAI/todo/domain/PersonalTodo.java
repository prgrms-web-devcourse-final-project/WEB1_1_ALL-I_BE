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

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "done", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean done;

    @Column(name = "todo_order", nullable = false, columnDefinition = "INT")
    private int todoOrder;

    @Column(name = "date", nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "start_time", nullable = true, columnDefinition = "TIME")
    private LocalTime startTime;

    // 객체 생성 시 JPA에서 생성 시간 자동 기입
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Builder
    private PersonalTodo(String title, boolean done, int todoOrder, LocalDate date, LocalTime startTime, User user, Category category){
        this.title = title;
        this.done = done;
        this.todoOrder = todoOrder;
        this.date = date;
        this.startTime = startTime;
        this.user = user;
        this.category = category;
    }

    public static PersonalTodo create(String title, LocalDate date, LocalTime startTime, User user, Category category){
        return PersonalTodo.builder()
                .title(title)
                .done(false)    //default
                .todoOrder(0)   //default
                .date(date)
                .startTime(startTime)
                .user(user)
                .category(category)
                .build();
    }
    //투두 상태 변경
    public void updatePersonalTodoState(boolean done){
        if(this.done != done){
            this.done = done;
        }
    }

    //투두 타이틀 변경
    public void updatePersonalTodoTitle(String title){
        this.title = title;
    }

    //투두 세부 항목 변경
    public void updatePersonalTodo(String title, Category category, LocalTime startTime, LocalDate date){
        this.title = title;
        this.category = category;
        this.startTime = startTime;
        this.date = date;
    }


}
