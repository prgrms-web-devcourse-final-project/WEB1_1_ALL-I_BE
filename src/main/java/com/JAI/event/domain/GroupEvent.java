package com.JAI.event.domain;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.JAI.group.domain.Group;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Table(name = "group_event")
public class GroupEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_event_id", columnDefinition = "BINARY(16)")
    private UUID groupEventId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "start_date",  nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(name = "start_time",  nullable = true, columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(name = "end_date",  nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @Column(name = "end_time",  nullable = true, columnDefinition = "TIME")
    private LocalTime endTime;

    @Column(name = "is_alarmed", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isAlarmed;

    @CreatedDate
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "groupEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEventMapping> groupEventMappings;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @Builder
    private GroupEvent(UUID groupEventId, String title, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Boolean isAlarmed, LocalDateTime createdAt, Group group) {
        this.groupEventId = groupEventId;
        this.title = title;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isAlarmed = isAlarmed;
        this.createdAt = createdAt;
        this.group = group;
    }

    public static GroupEvent create(String title, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Group group){
        return GroupEvent.builder()
                .title(title)
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .isAlarmed(false)   //default
                .group(group)
                .build();
    }


    public void updateGroup(Group group){
        if (this.group == null) {
            this.group = group;
        }
    }
}
