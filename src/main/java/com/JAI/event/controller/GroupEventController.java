package com.JAI.event.controller;

import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.GetOneGroupEventResDTO;
import com.JAI.event.service.GroupEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groupEvents")
@RequiredArgsConstructor
public class GroupEventController {
    private final GroupEventService groupEventService;

    @GetMapping("/{group_id}/events")
    public ApiResponse<GetOneGroupEventResDTO> getGroupEvents(@PathVariable("group_id") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupEvents(groupId, user.getUserId(), year, month));
    }

    @GetMapping("/{group_id}/events/{user_id}")
    public ApiResponse<?> getSomeOneGroupEvents(@PathVariable("group_id") UUID groupId, @PathVariable("user_id") UUID someoneUserId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupSomeOneEvents(groupId, someoneUserId, user.getUserId(), year, month));
    }

    @GetMapping
    public ApiResponse<?> getGroupMyEvents(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupMyEvents(user.getUserId(), year, month));
    }

    @PostMapping("/{group_id}/events")
    public ApiResponse<?> createGroupEvents(@PathVariable("group_id") UUID groupId, @Valid @RequestBody GroupEventCreateReqDTO groupEventCreateReqDTO, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.createGroupEvent(groupEventCreateReqDTO, groupId, user.getUserId()));
    }
}