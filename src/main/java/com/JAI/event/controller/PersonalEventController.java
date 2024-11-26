package com.JAI.event.controller;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.service.PersonalEventService;
import com.JAI.global.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    @GetMapping
    public ApiResponse<List<PersonalEventResDTO>> getPersonalEvent(@RequestParam String year, @RequestParam String month) {
        List<PersonalEventResDTO> personalEventResDTOs = personalEventService.getPersonalEventsForMonth(year, month);
        return ApiResponse.onSuccess(personalEventResDTOs);
    }

    @PostMapping
    public ApiResponse<Void> createPersonalEvent(@RequestBody PersonalEventCreateReqDTO personalCreateEventReqDTO) {
        personalEventService.createPersonalEvent(personalCreateEventReqDTO);
        return ApiResponse.onCreateSuccess();
    }

    // uuid 나중에 추가 해서 비교하고 수정 가능하게
    @PatchMapping
    public ApiResponse<PersonalEventResDTO> updatePersonalEvent(@Valid @RequestBody PersonalEventUpdateReqDTO personalEventUpdateReqDTO) {
        PersonalEventResDTO updatedPersonalEvent = personalEventService.updatePersonalEvent(personalEventUpdateReqDTO);
        return ApiResponse.onSuccess(updatedPersonalEvent);
    }

    // uuid 나중에 추가 해서 비교하고 수정 가능하게
    @DeleteMapping
    public ApiResponse<UUID> deletePersonalEvent(@RequestParam UUID personalEventId) {
        personalEventService.deletePersonalEvent(personalEventId);
        return ApiResponse.onDeleteSuccess(personalEventId);
    }
}
