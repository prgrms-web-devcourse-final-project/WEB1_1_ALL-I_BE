package com.JAI.todo.domain;


import com.JAI.group.domain.Group;
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
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)// 객체 생성 시 JPA에서 생성 시간 자동 기입
@Table(name = "group_todo")
@Entity
public class GroupTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="group_todo_id", columnDefinition = "BINARY(16)")
    private UUID groupTodoId;

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
    @JoinColumn(name = "group_id", referencedColumnName = "group_id",nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @OneToMany(mappedBy = "groupTodo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTodoMapping> groupTodoMappings;

    @Builder
    private GroupTodo(String title, boolean done, int todoOrder, LocalDate date, LocalTime startTime, Group group){
        this.title = title;
        this.done = done;
        this.todoOrder = todoOrder;
        this.date = date;
        this.startTime = startTime;
        this.group = group;
    }

    public static GroupTodo create(String title, LocalDate date, LocalTime startTime, Group group){
        return GroupTodo.builder()
                .title(title)
                .done(false)    //default
                .todoOrder(0)   //default
                .date(date)
                .startTime(startTime)
                .group(group)
                .build();
    }

    //투두 상태 변경
    public void updateGroupTodoState(boolean done){
        if(this.done != done){
            this.done = done;
        }
    }

}
