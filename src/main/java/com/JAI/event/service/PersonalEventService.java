package com.JAI.event.service;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;

import java.util.List;
import java.util.UUID;

public interface PersonalEventService {
    public void createPersonalEvent(PersonalEventCreateReqDTO personalEventCreateReqDTO);

    public PersonalEventResDTO updatePersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO);

    public void deletePersonalEvent(UUID personalEventId);

    public List<PersonalEventResDTO> getPersonalEventsForMonth(String year, String month, UUID userId);
}
