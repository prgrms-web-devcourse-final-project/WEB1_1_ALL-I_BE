package com.JAI.event.mapper;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import org.springframework.stereotype.Component;

@Component
public class PersonalEventConverter {
    public PersonalEvent personalCreateReqEventDTOToPersonalEvent(PersonalEventCreateReqDTO personalCreateReqEventDTO) {
        return PersonalEvent.builder()
                .title(personalCreateReqEventDTO.getTitle())
                .startDate(personalCreateReqEventDTO.getStartDate())
                .endDate(personalCreateReqEventDTO.getEndDate())
                .startTime(personalCreateReqEventDTO.getStartTime())
                .endTime(personalCreateReqEventDTO.getEndTime())
                .isAlarmed(personalCreateReqEventDTO.getIsAlarmed())
                .build();
    }

    public PersonalEvent personalUpdateReqEventDTOToPersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO, PersonalEvent personalEvent) {
        return PersonalEvent.builder()
                .personalEventId(personalEvent.getPersonalEventId())
                .title(personalEventUpdateReqDTO.getTitle() != null ? personalEventUpdateReqDTO.getTitle() : personalEvent.getTitle())
                .startDate(personalEventUpdateReqDTO.getStartDate() != null ? personalEventUpdateReqDTO.getStartDate() : personalEvent.getStartDate())
                .startTime(personalEventUpdateReqDTO.getStartTime() != null ? personalEventUpdateReqDTO.getStartTime() : personalEvent.getStartTime())
                .endDate(personalEventUpdateReqDTO.getEndDate() != null ? personalEventUpdateReqDTO.getEndDate() : personalEvent.getEndDate())
                .endTime(personalEventUpdateReqDTO.getEndTime() != null ? personalEventUpdateReqDTO.getEndTime() : personalEvent.getEndTime())
                .isAlarmed(personalEventUpdateReqDTO.getIsAlarmed() != null ? personalEventUpdateReqDTO.getIsAlarmed() : personalEvent.getIsAlarmed())
                .createdAt(personalEvent.getCreatedAt())
                .user(personalEvent.getUser())
                .category(personalEvent.getCategory())
                .build();
    }

    public PersonalEventResDTO personalEventResDTOTOToPersonalEvent(PersonalEvent personalEvent) {
        return PersonalEventResDTO.builder()
                .personalEventId(personalEvent.getPersonalEventId())
                .title(personalEvent.getTitle())
                .startDate(personalEvent.getStartDate())
                .endDate(personalEvent.getEndDate())
                .startTime(personalEvent.getStartTime())
                .endTime(personalEvent.getEndTime())
                .isAlarmed(personalEvent.getIsAlarmed())
                .createdAt(personalEvent.getCreatedAt())
                .categoryId(personalEvent.getCategory().getCategoryId())
                .personalEventId(personalEvent.getUser().getUserId())
                .build();
    }
}