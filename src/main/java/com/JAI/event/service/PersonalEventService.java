package com.JAI.event.service;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.domain.PersonalEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonalEventService {
    public void createPersonalEvent(PersonalEventCreateReqDTO personalEventCreateReqDTO, UUID userId);

    public PersonalEventResDTO updatePersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO, UUID personalEventId, UUID userId);

    public void deletePersonalEvent(UUID personalEventId, UUID userId);

    public List<PersonalEventResDTO> getPersonalEventsForMonth(String year, String month, UUID userId);

    public Optional<PersonalEvent> findPersonalEventById(UUID personalEventId);
}
