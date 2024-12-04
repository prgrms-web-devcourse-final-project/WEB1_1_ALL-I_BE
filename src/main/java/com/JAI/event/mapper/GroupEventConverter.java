package com.JAI.event.mapper;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.AllGroupSomeoneEventResDTO;
import com.JAI.event.DTO.response.OneGroupAllEventResDTO;
import com.JAI.event.DTO.response.OneGroupSomeoneEventResDTO;
import com.JAI.event.domain.GroupEvent;
import org.springframework.stereotype.Component;

@Component
public class GroupEventConverter {
    public OneGroupAllEventResDTO groupEventToOneGroupAllEventResDTO(GroupEvent groupEvent) {
        return OneGroupAllEventResDTO.builder()
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

    public OneGroupSomeoneEventResDTO groupEventToOneGroupSomeoneEventResDTO(GroupEvent groupEvent) {
        return OneGroupSomeoneEventResDTO.builder()
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

    public AllGroupSomeoneEventResDTO groupEventToAllGroupSomeoneEventResDTO(GroupEvent groupEvent) {
        return AllGroupSomeoneEventResDTO.builder()
                .groupEventId(groupEvent.getGroupEventId())
                .title(groupEvent.getTitle())
                .startDate(groupEvent.getStartDate())
                .endDate(groupEvent.getEndDate())
                .startTime(groupEvent.getStartTime())
                .endTime(groupEvent.getEndTime())
                .isAlarmed(groupEvent.getIsAlarmed())
                .createdAt(groupEvent.getCreatedAt())
                .groupId(groupEvent.getGroup().getGroupId())
                .build();
    }

    public GroupEvent groupEventCreateReqDTOToGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO) {
        return GroupEvent.builder()
                .title(groupEventCreateReqDTO.getTitle())
                .startDate(groupEventCreateReqDTO.getStartDate())
                .startTime(groupEventCreateReqDTO.getStartTime())
                .endDate(groupEventCreateReqDTO.getEndDate())
                .endTime(groupEventCreateReqDTO.getEndTime())
                .isAlarmed(groupEventCreateReqDTO.getIsAlarmed())
                .build();
    }
}