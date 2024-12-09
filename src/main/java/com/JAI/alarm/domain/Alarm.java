package com.JAI.alarm.domain;

import com.JAI.event.domain.GroupEventMapping;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.group.domain.GroupInvitation;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "alarm")
@ToString
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alarm_id", columnDefinition = "BINARY(16)")
    private UUID alarmId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private AlarmType type;

    @Column(name = "description", nullable = false, length = 800)
    private String description;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isRead;

    @Column(name = "is_sent", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isSent;

    @Column(name = "scheduled_time", columnDefinition = "TIMESTAMP")
    private ZonedDateTime scheduledTime;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "read_time", nullable = true, columnDefinition = "TIMESTAMP DEFAULT NULL")
    private ZonedDateTime readTime;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToOne
    @JoinColumn(name = "group_event_mapping_id", referencedColumnName = "group_event_mapping_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupEventMapping groupEventMapping;

    @OneToOne
    @JoinColumn(name = "personal_event_id", referencedColumnName = "personal_event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PersonalEvent personalEvent;

    @OneToOne
    @JoinColumn(name = "group_invitation_id", referencedColumnName = "group_invitation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupInvitation groupInvitation;

    @Builder
    private Alarm(UUID alarmId, AlarmType type, String description, ZonedDateTime scheduledTime, LocalDateTime createdAt, User user,
                  GroupEventMapping groupEventMapping, PersonalEvent personalEvent, GroupInvitation groupInvitation) {
        this.alarmId = alarmId;
        this.type = type;
        this.description = description;
        this.isRead = false;
        this.isSent = false;
        this.scheduledTime = scheduledTime;
        this.createdAt = createdAt;
        this.user = user;
        this.groupEventMapping = groupEventMapping;
        this.personalEvent = personalEvent;
        this.groupInvitation = groupInvitation;
    }

    // 알림 읽음 설정
    public void markAsRead() {
        this.isRead = true;
    }

    // 알림 보냄 설정
    public void markAsSent() {
        this.isSent = true;
    }

    // 읽음 시간 설정
    public void updateReadTime(ZonedDateTime now) {
        this.readTime = now;
    }
}