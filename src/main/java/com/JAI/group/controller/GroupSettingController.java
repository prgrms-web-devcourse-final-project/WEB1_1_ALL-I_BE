package com.JAI.group.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.service.GroupSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups-setting")
public class GroupSettingController {
    private final GroupSettingService groupSettingService;

    // TODO :: 그룹원 조회
    @GetMapping("/{groupId}")
    public ApiResponse<List<GroupMemberListRes>> getGroupMembers(@PathVariable UUID groupId) {
        return ApiResponse.onSuccess(groupSettingService.getGroupMembers(groupId));
    }
}
