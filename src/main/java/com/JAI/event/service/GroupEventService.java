package com.JAI.event.service;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.GetOneGroupEventResDTO;

import java.util.UUID;

public interface GroupEventService {
    public GetOneGroupEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month);

    public GetOneGroupEventResDTO getGroupSomeOneEvents(UUID groupId, UUID someoneUserId, UUID userId, String year, String month);

    public GetOneGroupEventResDTO getGroupMyEvents(UUID userId, String year, String month);

    public GetOneGroupEventResDTO getGroupEventsByGroupEventId(UUID userId, UUID groupEventId);

    public void createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId);

    public GetOneGroupEventResDTO updateGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID groupEventId, UUID userId);

    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId);
}
