package com.JAI.event.service;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
import com.JAI.event.DTO.response.GetAllGroupSomeoneEventResDTO;
import com.JAI.event.DTO.response.GetOneGroupEventResDTO;
import com.JAI.event.DTO.response.OneGroupAllEventResDTO;

import java.util.UUID;

public interface GroupEventService {
    public GetOneGroupEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month);

    public GetOneGroupEventResDTO getGroupSomeOneEvents(UUID groupId, UUID someoneUserId, UUID userId, String year, String month);

    public GetAllGroupSomeoneEventResDTO getGroupMyEvents(UUID userId, String year, String month);

    public OneGroupAllEventResDTO createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId);

    public OneGroupAllEventResDTO updateGroupEvent(UUID groupId, UUID groupEventId, GroupEventUpdateReqDTO groupEventCreateReqDTO, UUID userId);

    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId);
}
