package com.JAI.event.domain;

import com.JAI.category.domain.Category;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Table(name = "personal_event")
public class PersonalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "personal_event_id")
    private UUID personalEventId;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "end_time", nullable = true)
    private LocalTime endTime;

    @Column(name = "is_alarmed", nullable = false)
    private Boolean isAlarmed;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Builder
    private PersonalEvent(String description, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Boolean isAlarmed, User user, Category category) {
        this.description = description;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isAlarmed = isAlarmed;
        this.user = user;
        this.category = category;
    }

    public static PersonalEvent create(String description, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, User user, Category category) {
        return PersonalEvent.builder()
                .description(description)
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .isAlarmed(false)   //default
                .user(user)
                .category(category)
                .build();
    }
}
