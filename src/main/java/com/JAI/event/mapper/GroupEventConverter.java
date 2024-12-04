package com.JAI.event.mapper;

import com.JAI.event.DTO.response.GroupEventResDTO;
import com.JAI.event.domain.GroupEvent;
import org.springframework.stereotype.Component;

@Component
public class GroupEventConverter {
    public GroupEventResDTO groupEventToGroupEventResDTO(GroupEvent groupEvent) {
        return GroupEventResDTO.builder()
                .groupEventId(groupEvent.getGroupEventId())
                .title(groupEvent.getTitle())
                .startDate(groupEvent.getStartDate())
                .endDate(groupEvent.getEndDate())
                .startTime(groupEvent.getStartTime())
                .endTime(groupEvent.getEndTime())
                .isAlarmed(groupEvent.getIsAlarmed())
                .createdAt(groupEvent.getCreatedAt())
                .build();
    }
}