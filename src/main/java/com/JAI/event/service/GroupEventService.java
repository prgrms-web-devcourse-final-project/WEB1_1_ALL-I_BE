package com.JAI.event.service;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
import com.JAI.event.DTO.response.GetGroupEventResDTO;
import com.JAI.event.DTO.response.GetOneGroupEventResDTO;
import com.JAI.event.DTO.response.GroupEventResDTO;

import java.util.UUID;

public interface GroupEventService {
    public GetGroupEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month);

    public GetGroupEventResDTO getGroupSomeOneEvents(UUID groupId, UUID someoneUserId, UUID userId, String year, String month);

    public GetGroupEventResDTO getGroupMyEvents(UUID userId, String year, String month);

    public GroupEventResDTO createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId);

    public GroupEventResDTO updateGroupEvent(UUID groupId, UUID groupEventId, GroupEventUpdateReqDTO groupEventCreateReqDTO, UUID userId);

    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId);
}
