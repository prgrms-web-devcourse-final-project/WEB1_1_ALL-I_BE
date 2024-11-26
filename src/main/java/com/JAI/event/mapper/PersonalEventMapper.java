package com.JAI.event.mapper;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonalEventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    PersonalEvent personalCreateReqEventDTOToPersonalEvent(PersonalEventCreateReqDTO personalCreateReqEventDTO);

    @Mapping(target = "personalEventId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @AfterMapping
    default PersonalEvent personalUpdateReqEventDTOToPersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO, @MappingTarget PersonalEvent personalEvent) {
        if (personalEventUpdateReqDTO == null) {
            return null;
        }

        PersonalEvent.PersonalEventBuilder updatePersonalEvent = personalEvent.toBuilder();

        updatePersonalEvent.personalEventId(personalEvent.getPersonalEventId());
        updatePersonalEvent.title(personalEventUpdateReqDTO.getTitle() != null ? personalEventUpdateReqDTO.getTitle() : personalEvent.getTitle());
        updatePersonalEvent.startDate(personalEventUpdateReqDTO.getStartDate() != null ? personalEventUpdateReqDTO.getStartDate() : personalEvent.getStartDate());
        updatePersonalEvent.startTime(personalEventUpdateReqDTO.getStartTime() != null ? personalEventUpdateReqDTO.getStartTime() : personalEvent.getStartTime());
        updatePersonalEvent.endDate(personalEventUpdateReqDTO.getEndDate() != null ? personalEventUpdateReqDTO.getEndDate() : personalEvent.getEndDate());
        updatePersonalEvent.endTime(personalEventUpdateReqDTO.getEndTime() != null ? personalEventUpdateReqDTO.getEndTime() : personalEvent.getEndTime());
        updatePersonalEvent.isAlarmed(personalEventUpdateReqDTO.getIsAlarmed() != null ? personalEventUpdateReqDTO.getIsAlarmed() : personalEvent.getIsAlarmed());
        updatePersonalEvent.createdAt(personalEvent.getCreatedAt());
        updatePersonalEvent.user(personalEvent.getUser());
        updatePersonalEvent.category(personalEvent.getCategory());

        return updatePersonalEvent.build();
    }

    @Mapping(target = "categoryId", expression = "java(personalEvent.getCategory() != null ? personalEvent.getCategory().getCategoryId() : null)")
    @Mapping(target = "userId", expression = "java(personalEvent.getUser() != null ? personalEvent.getUser().getUserId() : null)")
    PersonalEventResDTO personalEventResDTOTOToPersonalEvent(PersonalEvent personalEvent);
}