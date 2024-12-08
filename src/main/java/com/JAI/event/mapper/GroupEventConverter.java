package com.JAI.event.mapper;

import com.JAI.event.DTO.GroupEventForAlarmDTO;
import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
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
                .groupId(groupEvent.getGroup().getGroupId())
                .categoryId(groupEvent.getGroup().getCategory().getCategoryId())
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

    public GroupEvent groupEventUpdateReqDTOToGroupEvent(GroupEventUpdateReqDTO groupEventUpdateReqDTO, GroupEvent existedGroupEvent) {
        return GroupEvent.builder()
                .groupEventId(groupEventUpdateReqDTO.getGroupEventId())
                .title(groupEventUpdateReqDTO.getTitle() == null ? existedGroupEvent.getTitle() : groupEventUpdateReqDTO.getTitle())
                .startDate(groupEventUpdateReqDTO.getStartDate() == null ? existedGroupEvent.getStartDate() : groupEventUpdateReqDTO.getStartDate())
                .startTime(groupEventUpdateReqDTO.getStartTime() == null ? null : groupEventUpdateReqDTO.getStartTime())
                .endDate(groupEventUpdateReqDTO.getEndDate() == null ? existedGroupEvent.getEndDate() : groupEventUpdateReqDTO.getEndDate())
                .endTime(groupEventUpdateReqDTO.getEndTime() == null ? null : groupEventUpdateReqDTO.getEndTime())
                .createdAt(existedGroupEvent.getCreatedAt())
                .isAlarmed(groupEventUpdateReqDTO.getIsAlarmed() == null ? existedGroupEvent.getIsAlarmed() : groupEventUpdateReqDTO.getIsAlarmed())
                .build();
    }

    public GroupEventForAlarmDTO groupEventToGroupEventDTO(GroupEvent groupEvent) {
        return GroupEventForAlarmDTO.builder()
                .groupEventId(groupEvent.getGroupEventId())
                .title(groupEvent.getTitle())
                .startDate(groupEvent.getStartDate())
                .startTime(groupEvent.getStartTime())
                .endDate(groupEvent.getEndDate())
                .endTime(groupEvent.getEndTime())
                .isAlarmed(groupEvent.getIsAlarmed())
                .createdAt(groupEvent.getCreatedAt())
                .group(groupEvent.getGroup())
                .categoryId(groupEvent.getGroup().getCategory().getCategoryId())
                .build();
    }

    public GroupEventResDTO GroupEventForAlarmDTOTogroupEventResDTO(GroupEventForAlarmDTO groupEventForAlarmDTO) {
        return GroupEventResDTO.builder()
                .groupEventId(groupEventForAlarmDTO.getGroupEventId())
                .title(groupEventForAlarmDTO.getTitle())
                .startDate(groupEventForAlarmDTO.getStartDate())
                .endDate(groupEventForAlarmDTO.getEndDate())
                .startTime(groupEventForAlarmDTO.getStartTime())
                .endTime(groupEventForAlarmDTO.getEndTime())
                .isAlarmed(groupEventForAlarmDTO.getIsAlarmed())
                .createdAt(groupEventForAlarmDTO.getCreatedAt())
                .groupId(groupEventForAlarmDTO.getGroupEventId())
                .categoryId(groupEventForAlarmDTO.getGroup().getCategory().getCategoryId())
                .assignedUserIds(groupEventForAlarmDTO.getAssignedUserIds())
                .build();
    }
}