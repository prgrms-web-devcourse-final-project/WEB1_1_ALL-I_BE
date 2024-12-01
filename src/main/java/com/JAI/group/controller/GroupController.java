package com.JAI.group.controller;


import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.service.GroupService;
import com.JAI.group.service.GroupSettigService;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final GroupSettigService groupSettigService;

    @PostMapping
    public ApiResponse<GroupCreateRes> createGroup(@RequestBody @Valid GroupCreateReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onCreateSuccess(groupService.createGroup(req, user));
    }

    @GetMapping
    public ApiResponse<List<GroupListRes>> getGroupList(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupService.getGroupList(user));
    }
}
