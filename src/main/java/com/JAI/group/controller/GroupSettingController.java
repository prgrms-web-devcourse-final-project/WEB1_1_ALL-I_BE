package com.JAI.group.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.service.GroupCheckService;
import com.JAI.group.service.GroupSettingService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups-setting")
public class GroupSettingController {
    private final GroupSettingService groupSettingService;
    private final GroupCheckService groupCheckService;

    // TODO :: 그룹원 조회
    @GetMapping("/{groupId}")
    public ApiResponse<List<GroupMemberListRes>> getGroupMembers(@PathVariable UUID groupId) {
        return ApiResponse.onSuccess(groupSettingService.getGroupMembers(groupId));
    }

    //본인 탈퇴
    @DeleteMapping("/{groupId}/quit")
    public ApiResponse<String> quitGroupMember(@PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user) {
        String returnMessage = groupCheckService.checkRoleAndHandleQuit(groupId, user);
        return ApiResponse.onDeleteSuccess(returnMessage);
    }

    //팀장이 강퇴
    @DeleteMapping("/{groupSettingId}/ejection")
    public ApiResponse<String> ejectionGroupMember(@PathVariable UUID groupSettingId, @AuthenticationPrincipal CustomUserDetails user) {
        groupSettingService.ejectionGroupMember(groupSettingId, user);
        return ApiResponse.onDeleteSuccess("성공적으로 탈퇴 시켰습니다.");
    }

}
