package com.JAI.event.service;

import com.JAI.category.exception.CategoryNotFoundException;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.event.exception.PersonalEventNotFoundException;
import com.JAI.event.mapper.PersonalEventMapper;
import com.JAI.event.repository.PersonalEventRepository;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalEventServiceImpl implements PersonalEventService {
    private final PersonalEventRepository personalEventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final PersonalEventMapper personalEventMapper;

    @Override
    public void createPersonalEvent(PersonalEventCreateReqDTO personalEventCreateReqDTO) {
        // 개인 일정 생성 요청 DTO 엔티티와 매핑
        PersonalEvent personalEvent = personalEventMapper.personalCreateReqEventDTOToPersonalEvent(personalEventCreateReqDTO);

        // userService를 통해 User를 가져오고 null 확인 및 예외 처리
        personalEvent.setUser(Optional.ofNullable(userService.getUserById(personalEventCreateReqDTO.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 사용자를 찾을 수 없습니다.", personalEventCreateReqDTO.getUserId())));

        // categoryService를 통해 Category를 설정
        personalEvent.setCategory(Optional.ofNullable(categoryService.getCategoryById(personalEventCreateReqDTO.getCategoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalEventCreateReqDTO.getCategoryId())));

        personalEventRepository.save(personalEvent);
    }

    @Override
    public PersonalEventResDTO updatePersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO) {
        // 기존 개인 일정 조회
        PersonalEvent existingEvent = personalEventRepository.findById(personalEventUpdateReqDTO.getPersonalEventId())
                .orElseThrow(() -> new PersonalEventNotFoundException("해당 ID의 개인 일정을 찾을 수 없습니다.", personalEventUpdateReqDTO.getPersonalEventId()));

        // MapStruct 매퍼를 사용한 업데이트
        PersonalEvent updatedPersonalEvent = personalEventMapper.personalUpdateReqEventDTOToPersonalEvent(personalEventUpdateReqDTO, existingEvent);

        // categoryService를 통해 Category를 설정
        Optional.ofNullable(personalEventUpdateReqDTO.getCategoryId())
                .map(categoryService::getCategoryById)
                .ifPresent(category -> updatedPersonalEvent.setCategory(Optional.of(category)
                        .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalEventUpdateReqDTO.getCategoryId()))));

        personalEventRepository.save(updatedPersonalEvent);

        return personalEventMapper.personalEventResDTOTOToPersonalEvent(updatedPersonalEvent);
    }

    @Override
    public void deletePersonalEvent(UUID personalEventId) {
        personalEventRepository.deleteById(personalEventId);
    }

    @Override
    public List<PersonalEventResDTO> getPersonalEventsForMonth(String year, String month) {
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<PersonalEvent> personalEvents = personalEventRepository.findAllByStartDateBetween(startDate, endDate);

        // PersonalEvent 리스트를 PersonalEventResDTO 리스트로 변환
        return personalEvents.stream()
                .map(personalEventMapper::personalEventResDTOTOToPersonalEvent)
                .collect(Collectors.toList());
    }
}
