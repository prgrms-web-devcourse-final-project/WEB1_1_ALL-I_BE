package com.JAI.event.controller;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
import com.JAI.event.DTO.response.GetGroupEventResDTO;
import com.JAI.event.DTO.response.GroupEventResDTO;
import com.JAI.event.service.GroupEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groupEvents")
@RequiredArgsConstructor
public class GroupEventController {
    private final GroupEventService groupEventService;

    @GetMapping("/{group_id}/events")
    @Operation(summary = "그룹 일정 한 달치 조회", description = "그룹 일정 한 달치 조회 요청 API / a 그룹 내의 사용자 b, c의 그룹 일정 조회")
    public ApiResponse<GetGroupEventResDTO> getGroupEvents(@PathVariable("group_id") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupEvents(groupId, user.getUserId(), year, month));
    }

    @GetMapping("/{group_id}/events/{user_id}")
    @Operation(summary = "그룹 내 특정 사용자에 할당된 그룹 일정 한 달치 조회", description = "그룹 내 특정 사용자에 할당된 그룹 일정 조회 요청 API / a 그룹의 b 사용자에게 할당된 그룹 일정 조회")
    public ApiResponse<GetGroupEventResDTO> getSomeOneGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("user_id") UUID someoneUserId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupSomeOneEvents(groupId, someoneUserId, user.getUserId(), year, month));
    }

    @GetMapping
    @Operation(summary = "사용자의 전체 그룹 일정 한 달치 조회", description = "사용자의 전체 그룹 일정 조회 요청 API / a 사용자가 속한 b, c 그룹 내에서 할당자가 a인 그룹 일정 조회")
    public ApiResponse<GetGroupEventResDTO> getGroupMyEvents(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user)
    {
        return ApiResponse.onSuccess(groupEventService.getGroupMyEvents(user.getUserId(), year, month));
    }

    @PostMapping("/{group_id}/events")
    @Operation(summary = "그룹 일정 생성", description = "그룹 일정 생성 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "그룹 일정(NOT NULL)", example = "동아리 회식"),
            @Parameter(name = "startDate", description = "그룹 일정 시작 날짜(NOT NULL)", example = "2024-12-09"),
            @Parameter(name = "endDate", description = "그룹 일정 종료 날짜(NOT NULL)", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 일정 시작 시간", example = "20:00:00"),
            @Parameter(name = "endTime", description = "그룹 일정 종료 시간", example = "22:00:00"),
            @Parameter(name = "isAlarmed", description = "그룹 일정 알림 여부(NOT NULL)", examples = {
                    @ExampleObject(name = "그룹 일정 알림 설정 ON", value = "true"),
                    @ExampleObject(name = "그룹 일정 알림 설정 OFF", value = "false")
            }),
            @Parameter(name = "groupId", description = "그룹 아이디(NOT NULL)", example = "2e7d8c6a-0acf-4b83-8366-bca08a145e22"),
            @Parameter(name = "assignedMemberList", description = "일정을 할당할 그룹 멤버 아이디 리스트(NOT NULL)", example =
                    """
                    ["32eda718-fc6c-47ed-82ce-697c5f3f6b28",
                    "8af30641-215d-42a6-aed6-006246e53d6e"]
                    """)
    })
    public ApiResponse<GroupEventResDTO> createGroupEvents(@PathVariable("group_id") UUID groupId, @Valid @RequestBody GroupEventCreateReqDTO groupEventCreateReqDTO,
                                                           @AuthenticationPrincipal CustomUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        return ApiResponse.onCreateSuccess(groupEventService.createGroupEvent(groupEventCreateReqDTO, groupId, user.getUserId()));
    }

    @Operation(summary = "그룹 일정 수정", description = "그룹 일정 수정 요청 API")
    @Parameters({
            @Parameter(name = "groupEventId", description = "그룹 일정 아이디(NOT NULL)", example = "069f3346-5d54-439c-8e27-a5a1c510843e"),
            @Parameter(name = "title", description = "그룹 일정", example = "동아리 회식"),
            @Parameter(name = "startDate", description = "그룹 일정 시작 날짜", example = "2024-12-09"),
            @Parameter(name = "endDate", description = "그룹 일정 종료 날짜", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 일정 시작 시간", example = "20:00:00"),
            @Parameter(name = "endTime", description = "그룹 일정 종료 시간", example = "22:00:00"),
            @Parameter(name = "isAlarmed", description = "그룹 일정 알림 여부", examples = {
                    @ExampleObject(name = "그룹 일정 알림 설정 ON", value = "true"),
                    @ExampleObject(name = "그룹 일정 알림 설정 OFF", value = "false")
            }),
            @Parameter(name = "assignedMemberList", description = "일정을 할당할 그룹 멤버 아이디 리스트", example =
                    """
                    ["32eda718-fc6c-47ed-82ce-697c5f3f6b28"]
                    """)
    })
    @PatchMapping("/{group_id}/events/{group_event_id}")
    public ApiResponse<GroupEventResDTO> updateGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("group_event_id") UUID groupEventId,
                                                           @Valid @RequestBody GroupEventUpdateReqDTO groupEventUpdateReqDTO, @AuthenticationPrincipal CustomUserDetails user,
                                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        return ApiResponse.onSuccess(groupEventService.updateGroupEvent(groupId, groupEventId, groupEventUpdateReqDTO, user.getUserId()));
    }

    @Operation(summary = "그룹 일정 삭제", description = "그룹 일정 삭제 요청 API")
    @DeleteMapping("/{group_id}/events/{group_event_id}")
    public ApiResponse<?> deleteGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("group_event_id") UUID groupEventId, @AuthenticationPrincipal CustomUserDetails user) {
        groupEventService.deleteGroupEvent(groupId, groupEventId, user.getUserId());
        return ApiResponse.onSuccess();
    }
}