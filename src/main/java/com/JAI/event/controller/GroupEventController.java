package com.JAI.event.controller;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
import com.JAI.event.DTO.response.GetGroupEventResDTO;
import com.JAI.event.DTO.response.GroupEventResDTO;
import com.JAI.event.service.GroupEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.global.status.ErrorStatus;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groupEvents")
@RequiredArgsConstructor
public class GroupEventController {
    private final GroupEventService groupEventService;

    @GetMapping("/{group_id}/events")
    public ApiResponse<GetGroupEventResDTO> getGroupEvents(@PathVariable("group_id") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupEvents(groupId, user.getUserId(), year, month));
    }

    @GetMapping("/{group_id}/events/{user_id}")
    public ApiResponse<GetGroupEventResDTO> getSomeOneGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("user_id") UUID someoneUserId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupSomeOneEvents(groupId, someoneUserId, user.getUserId(), year, month));
    }

    @GetMapping
    public ApiResponse<GetGroupEventResDTO> getGroupMyEvents(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupMyEvents(user.getUserId(), year, month));
    }

    @PostMapping("/{group_id}/events")
    public ApiResponse<GroupEventResDTO> createGroupEvents(@PathVariable("group_id") UUID groupId, @Validated @RequestBody GroupEventCreateReqDTO groupEventCreateReqDTO, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails user) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        return ApiResponse.onCreateSuccess(groupEventService.createGroupEvent(groupEventCreateReqDTO, groupId, user.getUserId()));
    }

    @PatchMapping("/{group_id}/events/{group_event_id}")
    public ApiResponse<GroupEventResDTO> updateGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("group_event_id") UUID groupEventId, @Valid @RequestBody GroupEventUpdateReqDTO groupEventUpdateReqDTO, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails user) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, errorMessage, null);
        }

        return ApiResponse.onSuccess(groupEventService.updateGroupEvent(groupId, groupEventId, groupEventUpdateReqDTO, user.getUserId()));
    }

    @DeleteMapping("/{group_id}/events/{group_event_id}")
    public ApiResponse<?> deleteGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("group_event_id") UUID groupEventId, @AuthenticationPrincipal CustomUserDetails user) {
        groupEventService.deleteGroupEvent(groupId, groupEventId, user.getUserId());
        return ApiResponse.onSuccess();
    }
}