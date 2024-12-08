package com.JAI.event.mapper;

import com.JAI.event.DTO.PersonalEventDTO;
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
                .startTime(personalEventUpdateReqDTO.getStartTime() != null ? personalEventUpdateReqDTO.getStartTime() : null)
                .endDate(personalEventUpdateReqDTO.getEndDate() != null ? personalEventUpdateReqDTO.getEndDate() : personalEvent.getEndDate())
                .endTime(personalEventUpdateReqDTO.getEndTime() != null ? personalEventUpdateReqDTO.getEndTime() : null)
                .isAlarmed(personalEventUpdateReqDTO.getIsAlarmed() != null ? personalEventUpdateReqDTO.getIsAlarmed() : personalEvent.getIsAlarmed())
                .createdAt(personalEvent.getCreatedAt())
                .user(personalEvent.getUser())
                .category(personalEvent.getCategory())
                .build();
    }

    public PersonalEventResDTO personalEventToPersonalEventResDTO(PersonalEvent personalEvent) {
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
                .userId(personalEvent.getUser().getUserId())
                .build();
    }

    public PersonalEventDTO personalEventToPersonalEventDTO(PersonalEvent personalEvent) {
        return PersonalEventDTO.builder()
                .personalEventId(personalEvent.getPersonalEventId())
                .title(personalEvent.getTitle())
                .startDate(personalEvent.getStartDate())
                .endDate(personalEvent.getEndDate())
                .startTime(personalEvent.getStartTime())
                .endTime(personalEvent.getEndTime())
                .isAlarmed(personalEvent.getIsAlarmed())
                .createdAt(personalEvent.getCreatedAt())
                .category(personalEvent.getCategory())
                .user(personalEvent.getUser())
                .build();
    }

    public PersonalEvent personalEventDTOToPersonalEvent(PersonalEventDTO personalEventDTO) {
        return PersonalEvent.builder()
                .personalEventId(personalEventDTO.getPersonalEventId())
                .title(personalEventDTO.getTitle())
                .startDate(personalEventDTO.getStartDate())
                .endDate(personalEventDTO.getEndDate())
                .startTime(personalEventDTO.getStartTime())
                .endTime(personalEventDTO.getEndTime())
                .isAlarmed(personalEventDTO.getIsAlarmed())
                .createdAt(personalEventDTO.getCreatedAt())
                .category(personalEventDTO.getCategory())
                .user(personalEventDTO.getUser())
                .build();
    }

    public PersonalEventResDTO personalEventDTOToPersonalEventResDTO(PersonalEventDTO personalEventDTO) {
        return PersonalEventResDTO.builder()
                .personalEventId(personalEventDTO.getPersonalEventId())
                .title(personalEventDTO.getTitle())
                .startDate(personalEventDTO.getStartDate())
                .endDate(personalEventDTO.getEndDate())
                .startTime(personalEventDTO.getStartTime())
                .endTime(personalEventDTO.getEndTime())
                .isAlarmed(personalEventDTO.getIsAlarmed())
                .createdAt(personalEventDTO.getCreatedAt())
                .categoryId(personalEventDTO.getCategory().getCategoryId())
                .userId(personalEventDTO.getUser().getUserId())
                .build();
    }
}