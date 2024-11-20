package com.JAI.event.domain;
import com.JAI.group.domain.Group;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "group_event_mapping")
public class GroupEventMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_event_mapping_id")
    private UUID groupEventMappingId;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @ManyToOne
    @JoinColumn(name = "group_event_id", referencedColumnName = "group_event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupEvent groupEvent;

    @Builder
    private GroupEventMapping(Group group, GroupEvent groupEvent) {
        this.group = group;
        this.groupEvent = groupEvent;
    }

    public static GroupEventMapping create(Group group, GroupEvent groupEvent) {
        return GroupEventMapping.builder()
                .group(group)
                .groupEvent(groupEvent)
                .build();
    }
}
