package com.JAI.alarm.domain;

import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alarm_id", columnDefinition = "BINARY(16)")
    private UUID alarmId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private AlarmType type;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isRead;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    private Alarm(AlarmType type, String description, Boolean isRead, LocalDateTime updatedAt) {
        this.type = type;
        this.description = description;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }

    public static Alarm create(AlarmType type, String description) {
        return Alarm.builder()
                .type(type)
                .description(description)
                .isRead(false)
                .build();
    }
}