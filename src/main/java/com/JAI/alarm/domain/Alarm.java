package com.JAI.alarm.domain;

import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)// 객체 생성 시 JPA에서 생성 시간 자동 기입
@Getter
@Entity
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alarm_id")
    private UUID alarmId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AlarmType type;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_read")
    private Integer isRead;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    private Alarm(AlarmType type, String description, Integer isRead, LocalDateTime updatedAt) {
        this.type = type;
        this.description = description;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }

    public static Alarm create(AlarmType type, String description, Integer isRead, LocalDateTime updatedAt) {
        return Alarm.builder()
                .type(type)
                .description(description)
                .isRead(isRead) //초기값이 null인건가요? // TODO ::
                .build();
    }
    public boolean isRead() {
        return isRead != null && isRead == 1;
    }
}