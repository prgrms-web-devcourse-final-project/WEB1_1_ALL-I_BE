package com.JAI.event.service;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.GetOneGroupAllEventResDTO;

import java.util.List;
import java.util.UUID;

public interface GroupEventService {
    public GetOneGroupAllEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month);

    public GetOneGroupAllEventResDTO getGroupMyEvents(UUID userId, String year, String month);

    public GetOneGroupAllEventResDTO getGroupEventsByGroupEventId(UUID userId, UUID groupEventId);

    public void createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId);

    public GetOneGroupAllEventResDTO updateGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID groupEventId, UUID userId);

    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId);
}
