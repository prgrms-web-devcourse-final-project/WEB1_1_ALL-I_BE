package com.JAI.event.service;

import com.JAI.event.DTO.PersonalCreateEventReqDTO;
import com.JAI.event.domain.PersonalEvent;

import java.util.UUID;

public interface PersonalEventService {
    public void createPersonalEvent(PersonalCreateEventReqDTO personalCreateEventReqDTO);
    public void updatePersonalEvent(UUID userId, PersonalEvent personalEvent);
    public void deletePersonalEvent(UUID userId, UUID personalEventId);
}
