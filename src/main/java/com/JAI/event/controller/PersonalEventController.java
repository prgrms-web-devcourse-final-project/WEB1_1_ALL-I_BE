package com.JAI.event.controller;

import com.JAI.event.DTO.request.PersonalCreateEventReqDTO;
import com.JAI.event.DTO.request.PersonalUpdateEventReqDTO;
import com.JAI.event.DTO.response.PersonalUpdateEventResDTO;
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
    public List<Object> personalEvent() {
        return null;
    }

    @PostMapping
    public ApiResponse<Void> createPersonalEvent(@RequestBody PersonalCreateEventReqDTO personalCreateEventReqDTO) {
        personalEventService.createPersonalEvent(personalCreateEventReqDTO);
        return ApiResponse.onCreateSuccess();
    }

    // uuid 나중에 추가 해서 비교하고 수정 가능하게
    @PatchMapping
    public ApiResponse<PersonalUpdateEventResDTO> updatePersonalEvent(@Valid @RequestBody PersonalUpdateEventReqDTO personalUpdateEventReqDTO) {
        PersonalUpdateEventResDTO updatedPersonalEvent = personalEventService.updatePersonalEvent(personalUpdateEventReqDTO);
        return ApiResponse.onSuccess(updatedPersonalEvent);
    }

    // uuid 나중에 추가 해서 비교하고 수정 가능하게
    @DeleteMapping
    public ApiResponse<UUID> deletePersonalEvent(@RequestParam UUID personalEventId) {
        personalEventService.deletePersonalEvent(personalEventId);
        return ApiResponse.onDeleteSuccess(personalEventId);
    }
}
