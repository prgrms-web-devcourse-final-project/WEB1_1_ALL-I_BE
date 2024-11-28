package com.JAI.event.domain;

import com.JAI.category.domain.Category;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
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
    @Column(name = "personal_event_id", columnDefinition = "BINARY(16)")
    private UUID personalEventId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "start_date", nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(name = "start_time", nullable = true, columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(name = "end_date", nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @Column(name = "end_time", nullable = true, columnDefinition = "TIME")
    private LocalTime endTime;

    @Column(name = "is_alarmed", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isAlarmed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    public void setUser(final User user) {
        if (this.user != null) {
            throw new IllegalStateException("User has already been set.");
        }
        this.user = user;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    @Builder
    private PersonalEvent(String title, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Boolean isAlarmed, User user, Category category) {
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isAlarmed = isAlarmed;
        this.user = user;
        this.category = category;
    }

    public static PersonalEvent create(String title, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, User user, Category category) {
        return PersonalEvent.builder()
                .title(title)
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
