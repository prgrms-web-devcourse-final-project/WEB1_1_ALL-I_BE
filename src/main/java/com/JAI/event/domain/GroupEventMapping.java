package com.JAI.event.domain;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupSetting;
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
    @Column(name = "group_event_mapping_id", columnDefinition = "BINARY(16)")
    private UUID groupEventMappingId;

    @ManyToOne
    @JoinColumn(name = "group_event_id", referencedColumnName = "group_event_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupEvent groupEvent;

    @ManyToOne
    @JoinColumn(name = "group_setting_id", referencedColumnName = "group_setting_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private GroupSetting groupSetting;

    @Builder
    private GroupEventMapping(GroupSetting groupSettingg, GroupEvent groupEvent) {
        this.groupSetting = groupSetting;
        this.groupEvent = groupEvent;
    }

    public static GroupEventMapping create(GroupSetting groupSetting, GroupEvent groupEvent) {
        return GroupEventMapping.builder()
                .groupSettingg(groupSetting)
                .groupEvent(groupEvent)
                .build();
    }
}
