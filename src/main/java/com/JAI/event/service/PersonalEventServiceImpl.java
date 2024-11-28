package com.JAI.event.service;

import com.JAI.category.exception.CategoryNotFoundException;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.event.exception.PersonalEventBadRequestException;
import com.JAI.event.exception.PersonalEventNotFoundException;
import com.JAI.event.exception.PersonalEventNotOwnerException;
import com.JAI.event.mapper.PersonalEventConverter;
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
    private final PersonalEventConverter personalEventConverter;

    @Override
    public void createPersonalEvent(PersonalEventCreateReqDTO personalEventCreateReqDTO, UUID userId) {
        // 본인 외 다른 사용자의 개인 일정 생성 요청 시 에러 처리
        if (!personalEventCreateReqDTO.getUserId().equals(userId)) {
            throw new PersonalEventNotOwnerException("다른 사용자의 개인 일정을 생성할 수 없습니다.");
        }

        // 개인 일정 생성 요청 DTO 엔티티와 매핑
        PersonalEvent personalEvent = personalEventConverter.personalCreateReqEventDTOToPersonalEvent(personalEventCreateReqDTO);

        // userService를 통해 User를 가져오고 null 확인 및 예외 처리
        personalEvent.setUser(Optional.ofNullable(userService.getUserById(personalEventCreateReqDTO.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 사용자를 찾을 수 없습니다.", personalEventCreateReqDTO.getUserId())));

        // categoryService를 통해 Category를 설정
        personalEvent.setCategory(Optional.ofNullable(categoryService.getCategoryById(personalEventCreateReqDTO.getCategoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalEventCreateReqDTO.getCategoryId())));

        personalEventRepository.save(personalEvent);
    }

    @Override
    public PersonalEventResDTO updatePersonalEvent(PersonalEventUpdateReqDTO personalEventUpdateReqDTO, UUID personalEventId, UUID userId) {
        // api 요청 시 일정 id와 dto의 id가 일치하지 않을 시 에러 처리
        if (!personalEventId.equals(personalEventUpdateReqDTO.getPersonalEventId())) {
            throw new PersonalEventBadRequestException("잘못된 개인 일정 수정 요청입니다");
        }
        
        // 기존 개인 일정 조회
        PersonalEvent existingEvent = personalEventRepository.findById(personalEventId)
                .orElseThrow(() -> new PersonalEventNotFoundException("해당 ID의 개인 일정을 찾을 수 없습니다.", personalEventUpdateReqDTO.getPersonalEventId()));

        // 본인 외 다른 사용자의 개인 일정 수정 요청 시 에러 처리
        if (!existingEvent.getUser().getUserId().equals(userId)) {
            throw new PersonalEventNotOwnerException("다른 사용자의 개인 일정을 수정할 수 없습니다.");
        }

        // MapStruct 매퍼를 사용한 업데이트
        PersonalEvent updatedPersonalEvent = personalEventConverter.personalUpdateReqEventDTOToPersonalEvent(personalEventUpdateReqDTO, existingEvent);

        // categoryService를 통해 Category를 설정
        Optional.ofNullable(personalEventUpdateReqDTO.getCategoryId())
                .map(categoryService::getCategoryById)
                .ifPresent(category -> updatedPersonalEvent.setCategory(Optional.of(category)
                        .orElseThrow(() -> new CategoryNotFoundException("해당 ID의 카테고리를 찾을 수 없습니다.", personalEventUpdateReqDTO.getCategoryId()))));

        personalEventRepository.save(updatedPersonalEvent);

        return personalEventConverter.personalEventResDTOTOToPersonalEvent(updatedPersonalEvent);
    }

    @Override
    public void deletePersonalEvent(UUID personalEventId, UUID userId) {
        // 기존 개인 일정 조회
        PersonalEvent existingEvent = personalEventRepository.findById(personalEventId)
                .orElseThrow(() -> new PersonalEventNotFoundException("해당 ID의 개인 일정을 찾을 수 없습니다.", personalEventId));

        // api 요청 시 일정 id와 삭제할 일정의 id가 일치하지 않을 시 에러 처리
        if (!personalEventId.equals(existingEvent.getPersonalEventId())) {
            throw new PersonalEventBadRequestException("잘못된 개인 일정 수정 요청입니다");
        }
        
        // 본인 외 다른 사용자의 개인 일정 삭제 요청 시 에러 처리
        if (!existingEvent.getUser().getUserId().equals(userId)) {
            throw new PersonalEventNotOwnerException("다른 사용자의 개인 일정을 생성할 수 없습니다.");
        }
        
        personalEventRepository.deleteById(personalEventId);
    }

    @Override
    public List<PersonalEventResDTO> getPersonalEventsForMonth(String year, String month, UUID userId) {
        // 사용자를 찾을 수 없을 시 사용
        Optional.ofNullable(userService.getUserById(userId))
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 사용자를 찾을 수 없습니다.", userId));
        
        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<PersonalEvent> personalEvents = personalEventRepository.findAllByStartDateBetweenAndUser_UserId(startDate, endDate, userId);

        // PersonalEvent 리스트를 PersonalEventResDTO 리스트로 변환
        return personalEvents.stream()
                .map(personalEventConverter::personalEventResDTOTOToPersonalEvent)
                .collect(Collectors.toList());
    }
}
