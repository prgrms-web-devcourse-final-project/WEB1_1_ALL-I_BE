package com.JAI.event.service;

import com.JAI.event.DTO.request.PersonalCreateEventReqDTO;
import com.JAI.event.DTO.request.PersonalUpdateEventReqDTO;
import com.JAI.event.DTO.response.PersonalUpdateEventResDTO;
import com.JAI.event.domain.PersonalEvent;

import java.util.UUID;

public interface PersonalEventService {
    public void createPersonalEvent(PersonalCreateEventReqDTO personalCreateEventReqDTO);
    public PersonalUpdateEventResDTO updatePersonalEvent(PersonalUpdateEventReqDTO personalUpdateEventReqDTO);
    public void deletePersonalEvent(UUID personalEventId);
}
