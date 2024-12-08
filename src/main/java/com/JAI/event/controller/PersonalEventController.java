package com.JAI.event.controller;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.service.PersonalEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    @GetMapping
    public ApiResponse<List<PersonalEventResDTO>> getPersonalEvent(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        List<PersonalEventResDTO> personalEventDTOS = personalEventService.getPersonalEventsForMonth(year, month, user.getUserId());
        return ApiResponse.onSuccess(personalEventDTOS);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createPersonalEvent(
            @Valid @RequestBody PersonalEventCreateReqDTO personalCreateEventReqDTO, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails user) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        personalEventService.createPersonalEvent(personalCreateEventReqDTO, user.getUserId());
        return ApiResponse.onCreateSuccess();
    }

    @PatchMapping("{event_id}")
    public ApiResponse<PersonalEventResDTO> updatePersonalEvent(@PathVariable("event_id") UUID personalEventId,
                                                                @Valid @RequestBody PersonalEventUpdateReqDTO personalEventUpdateReqDTO,
                                                                BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails user) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        PersonalEventResDTO updatedPersonalEvent = personalEventService.updatePersonalEvent(personalEventUpdateReqDTO, personalEventId, user.getUserId());
        return ApiResponse.onSuccess(updatedPersonalEvent);
    }

    @DeleteMapping("{event_id}")
    public ApiResponse<UUID> deletePersonalEvent(@PathVariable("event_id") UUID personalEventId, @AuthenticationPrincipal CustomUserDetails user) {
        personalEventService.deletePersonalEvent(personalEventId, user.getUserId());
        return ApiResponse.onDeleteSuccess(personalEventId);
    }
}
