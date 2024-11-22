package com.JAI.event.mapper;

import com.JAI.event.DTO.PersonalCreateEventReqDTO;
import com.JAI.event.domain.PersonalEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonalEventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    PersonalEvent personalCreateReqEventDTOToPersonalEvent(PersonalCreateEventReqDTO personalCreateReqEventDTO);
}
