package com.JAI.event.controller;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.service.PersonalEventService;
import com.JAI.global.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    // 추후 로그인 달면 userid 부분 수정
    @GetMapping
    public ApiResponse<List<PersonalEventResDTO>> getPersonalEvent(@RequestParam String year, @RequestParam String month, @RequestParam UUID userId) {
        List<PersonalEventResDTO> personalEventResDTOs = personalEventService.getPersonalEventsForMonth(year, month, userId);
        return ApiResponse.onSuccess(personalEventResDTOs);
    }

    // 추후 로그인 달면 userid 부분 수정
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createPersonalEvent(
            @Valid @RequestBody PersonalEventCreateReqDTO personalCreateEventReqDTO, @RequestParam UUID userId) {
        personalEventService.createPersonalEvent(personalCreateEventReqDTO, userId);
        return ApiResponse.onCreateSuccess();
    }

    // 추후 로그인 달면 userid 부분 수정
    @PatchMapping("{event_id}")
    public ApiResponse<PersonalEventResDTO> updatePersonalEvent(@PathVariable("event_id") UUID personalEventId,
                                                                @Valid @RequestBody PersonalEventUpdateReqDTO personalEventUpdateReqDTO, @RequestParam UUID userId) {
        PersonalEventResDTO updatedPersonalEvent = personalEventService.updatePersonalEvent(personalEventUpdateReqDTO, personalEventId, userId);
        return ApiResponse.onSuccess(updatedPersonalEvent);
    }

    // 추후 로그인 달면 userid 부분 수정
    @DeleteMapping("{event_id}")
    public ApiResponse<UUID> deletePersonalEvent(@PathVariable("event_id") UUID personalEventId, @RequestParam UUID userId) {
        personalEventService.deletePersonalEvent(personalEventId, userId);
        return ApiResponse.onDeleteSuccess(personalEventId);
    }
}
