package com.JAI.event.controller;

import com.JAI.event.service.GroupEventService;
import com.JAI.global.controller.ApiResponse;
import com.JAI.user.service.dto.CustomUserDetails;
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
    public ApiResponse<?> getGroupEvents(@PathVariable("group_id") UUID group_id, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupEventService.getGroupEvents(group_id, user.getUserId(), year, month));
    }

    @GetMapping
    public ApiResponse<?> getGroupMyEvents(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user) {
        return null;
    }
}