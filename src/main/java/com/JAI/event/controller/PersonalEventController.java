package com.JAI.event.controller;

import com.JAI.event.DTO.request.PersonalEventCreateReqDTO;
import com.JAI.event.DTO.request.PersonalEventUpdateReqDTO;
import com.JAI.event.DTO.response.PersonalEventResDTO;
import com.JAI.event.service.PersonalEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    @GetMapping
    @Operation(summary = "개인 일정 한 달치 조회", description = "개인 일정 한 달치 조회 요청 API")
    public ApiResponse<List<PersonalEventResDTO>> getPersonalEvent(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        List<PersonalEventResDTO> personalEventDTOS = personalEventService.getPersonalEventsForMonth(year, month, user.getUserId());
        return ApiResponse.onSuccess(personalEventDTOS);
    }

    @PostMapping
    @Operation(summary = "개인 일정 생성", description = "개인 일정 생성 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "개인 일정(NOT NULL)", example = "개발 세션 참석"),
            @Parameter(name = "startDate", description = "개인 일정 시작 날짜(NOT NULL)", example = "2024-12-09"),
            @Parameter(name = "endDate", description = "개인 일정 종료 날짜(NOT NULL)", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "개인 일정 시작 시간", example = "20:00:00"),
            @Parameter(name = "endTime", description = "개인 일정 종료 시간", example = "22:00:00"),
            @Parameter(name = "isAlarmed", description = "개인 일정 알림 여부(NOT NULL)", examples = {
                    @ExampleObject(name = "그룹 일정 알림 설정 ON", value = "true"),
                    @ExampleObject(name = "그룹 일정 알림 설정 OFF", value = "false")
            }),
            @Parameter(name = "categoryId", description = "카테고리 아이디(NOT NULL)", example = "2e7d8c6a-0acf-4b83-8366-bca08a145e22"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createPersonalEvent(
            @Valid @RequestBody PersonalEventCreateReqDTO personalCreateEventReqDTO, @AuthenticationPrincipal CustomUserDetails user) {
        personalEventService.createPersonalEvent(personalCreateEventReqDTO, user.getUserId());
        return ApiResponse.onCreateSuccess();
    }

    @PatchMapping("{event_id}")
    @Operation(summary = "개인 일정 수정", description = "개인 일정 수정 요청 API")
    @Parameters({
            @Parameter(name = "personalEventId", description = "개인 일정 아이디(NOT NULL)", example = "4ce7eb71-1b74-4ae3-aebf-7b179a825b5b"),
            @Parameter(name = "title", description = "개인 일정", example = "개발 세션"),
            @Parameter(name = "startDate", description = "개인 일정 시작 날짜", example = "2024-12-09"),
            @Parameter(name = "endDate", description = "개인 일정 종료 날짜", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "개인 일정 시작 시간", example = "20:00:00"),
            @Parameter(name = "endTime", description = "개인 일정 종료 시간", example = "22:00:00"),
            @Parameter(name = "isAlarmed", description = "개인 일정 알림 여부", examples = {
                    @ExampleObject(name = "그룹 일정 알림 설정 ON", value = "true"),
                    @ExampleObject(name = "그룹 일정 알림 설정 OFF", value = "false")
            }),
            @Parameter(name = "categoryId", description = "카테고리 아이디(NOT NULL)", example = "3e7d8c6a-0acf-4b83-8366-bca08a145e22"),
    })
    public ApiResponse<PersonalEventResDTO> updatePersonalEvent(@PathVariable("event_id") UUID personalEventId,
                                                                @Valid @RequestBody PersonalEventUpdateReqDTO personalEventUpdateReqDTO, @AuthenticationPrincipal CustomUserDetails user) {
        PersonalEventResDTO updatedPersonalEvent = personalEventService.updatePersonalEvent(personalEventUpdateReqDTO, personalEventId, user.getUserId());
        return ApiResponse.onSuccess(updatedPersonalEvent);
    }

    @DeleteMapping("{event_id}")
    @Operation(summary = "개인 일정 삭제", description = "개인 일정 삭제 요청 API")
    public ApiResponse<UUID> deletePersonalEvent(@PathVariable("event_id") UUID personalEventId, @AuthenticationPrincipal CustomUserDetails user) {
        personalEventService.deletePersonalEvent(personalEventId, user.getUserId());
        return ApiResponse.onDeleteSuccess(personalEventId);
    }
}
