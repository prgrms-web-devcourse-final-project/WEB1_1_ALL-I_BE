package com.JAI.event.service;

import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.PersonalCreateEventReqDTO;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.event.mapper.PersonalEventMapper;
import com.JAI.event.repository.PersonalEventRepository;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonalEventServiceImpl implements PersonalEventService {
    private final PersonalEventRepository personalEventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final PersonalEventMapper personalEventMapper;

    @Override
    public void createPersonalEvent(PersonalCreateEventReqDTO personalCreateEventReqDTO) {
        PersonalEvent personalEvent = personalEventMapper.personalCreateReqEventDTOToPersonalEvent(personalCreateEventReqDTO);
        personalEvent.setUser(userService.getUserById(personalCreateEventReqDTO.getUserId()));
        personalEvent.setCategory(categoryService.getCategoryById(personalCreateEventReqDTO.getCategoryId()));
        personalEventRepository.save(personalEvent);
        System.out.println(personalEvent);
    }

    @Override
    public void updatePersonalEvent(UUID userId, PersonalEvent personalEvent) {

    }

    @Override
    public void deletePersonalEvent(UUID userId, UUID personalEventId) {

    }
}
