package com.JAI.event.service;

import com.JAI.category.exception.CategoryNotFoundException;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.request.PersonalCreateEventReqDTO;
import com.JAI.event.DTO.request.PersonalUpdateEventReqDTO;
import com.JAI.event.DTO.response.PersonalUpdateEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.event.exception.PersonalEventNotFoundException;
import com.JAI.event.mapper.PersonalEventMapper;
import com.JAI.event.repository.PersonalEventRepository;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
        // 개인 일정 생성 요청 DTO 엔티티와 매핑
        PersonalEvent personalEvent = personalEventMapper.personalCreateReqEventDTOToPersonalEvent(personalCreateEventReqDTO);

        // userService를 통해 User를 가져오고 null 확인 및 예외 처리
        personalEvent.setUser(Optional.ofNullable(userService.getUserById(personalCreateEventReqDTO.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 사용자를 찾을 수 없습니다.", personalCreateEventReqDTO.getUserId())));

        // categoryService를 통해 Category를 설정
        personalEvent.setCategory(Optional.ofNullable(categoryService.getCategoryById(personalCreateEventReqDTO.getCategoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalCreateEventReqDTO.getCategoryId())));

        personalEventRepository.save(personalEvent);
    }

    @Override
    public PersonalUpdateEventResDTO updatePersonalEvent(PersonalUpdateEventReqDTO personalUpdateEventReqDTO) {
        // 기존 개인 일정 조회
        PersonalEvent existingEvent = personalEventRepository.findById(personalUpdateEventReqDTO.getPersonalEventId())
                .orElseThrow(() -> new PersonalEventNotFoundException("해당 ID의 개인 일정을 찾을 수 없습니다.", personalUpdateEventReqDTO.getPersonalEventId()));

        // MapStruct 매퍼를 사용한 업데이트
        PersonalEvent updatedPersonalEvent = personalEventMapper.personalUpdateReqEventDTOToPersonalEvent(personalUpdateEventReqDTO, existingEvent);

        // categoryService를 통해 Category를 설정
        Optional.ofNullable(personalUpdateEventReqDTO.getCategoryId())
                .map(categoryService::getCategoryById)
                .ifPresent(category -> updatedPersonalEvent.setCategory(Optional.of(category)
                        .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalUpdateEventReqDTO.getCategoryId()))));

        personalEventRepository.save(updatedPersonalEvent);

        PersonalUpdateEventResDTO personalUpdateEventResDTO = personalEventMapper.personalUpdateEventResDTOTOToPersonalEvent(updatedPersonalEvent);
        personalUpdateEventResDTO.setCategoryId(updatedPersonalEvent.getCategory().getCategoryId());
        personalUpdateEventResDTO.setUserId(updatedPersonalEvent.getUser().getUserId());

        return personalUpdateEventResDTO;
    }

    @Override
    public void deletePersonalEvent(UUID personalEventId) {
        personalEventRepository.deleteById(personalEventId);
    }
}
