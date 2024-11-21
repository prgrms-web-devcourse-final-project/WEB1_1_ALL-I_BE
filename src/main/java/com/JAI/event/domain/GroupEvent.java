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
    @Column(name = "group_event_id")
    private UUID groupEventId;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "start_date",  nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time",  nullable = true)
    private LocalTime startTime;

    @Column(name = "end_date",  nullable = false)
    private LocalDate endDate;

    @Column(name = "end_time",  nullable = true)
    private LocalTime endTime;

    @Column(name = "is_alarmed", nullable = false)
    private Boolean isAlarmed;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "groupEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEventMapping> groupEventMappings;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @Builder
    private GroupEvent(String description, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Boolean isAlarmed, Group group) {
        this.description = description;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.isAlarmed = isAlarmed;
        this.group = group;
    }

    public static GroupEvent create(String description, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, Group group){
        return GroupEvent.builder()
                .description(description)
                .startDate(startDate)
                .startTime(startTime)
                .endDate(endDate)
                .endTime(endTime)
                .isAlarmed(false)   //default
                .group(group)
                .build();
    }

}
