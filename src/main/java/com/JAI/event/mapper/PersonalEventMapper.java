package com.JAI.event.mapper;

import com.JAI.event.DTO.request.PersonalCreateEventReqDTO;
import com.JAI.event.DTO.request.PersonalUpdateEventReqDTO;
import com.JAI.event.DTO.response.PersonalUpdateEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonalEventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    PersonalEvent personalCreateReqEventDTOToPersonalEvent(PersonalCreateEventReqDTO personalCreateReqEventDTO);

    @Mapping(target = "personalEventId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @AfterMapping
    default PersonalEvent personalUpdateReqEventDTOToPersonalEvent(PersonalUpdateEventReqDTO personalUpdateEventReqDTO, @MappingTarget PersonalEvent personalEvent) {
        if (personalUpdateEventReqDTO == null) {
            return null;
        }

        PersonalEvent.PersonalEventBuilder updatePersonalEvent = personalEvent.toBuilder();

        updatePersonalEvent.personalEventId(personalEvent.getPersonalEventId());
        updatePersonalEvent.title(personalUpdateEventReqDTO.getTitle() != null ? personalUpdateEventReqDTO.getTitle() : personalEvent.getTitle());
        updatePersonalEvent.startDate(personalUpdateEventReqDTO.getStartDate() != null ? personalUpdateEventReqDTO.getStartDate() : personalEvent.getStartDate());
        updatePersonalEvent.startTime(personalUpdateEventReqDTO.getStartTime() != null ? personalUpdateEventReqDTO.getStartTime() : personalEvent.getStartTime());
        updatePersonalEvent.endDate(personalUpdateEventReqDTO.getEndDate() != null ? personalUpdateEventReqDTO.getEndDate() : personalEvent.getEndDate());
        updatePersonalEvent.endTime(personalUpdateEventReqDTO.getEndTime() != null ? personalUpdateEventReqDTO.getEndTime() : personalEvent.getEndTime());
        updatePersonalEvent.isAlarmed(personalUpdateEventReqDTO.getIsAlarmed() != null ? personalUpdateEventReqDTO.getIsAlarmed() : personalEvent.getIsAlarmed());
        updatePersonalEvent.user(personalEvent.getUser());
        updatePersonalEvent.category(personalEvent.getCategory());

        return updatePersonalEvent.build();
    }

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    PersonalUpdateEventResDTO personalUpdateEventResDTOTOToPersonalEvent(PersonalEvent personalEvent);
}