package com.JAI.group.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.request.GroupMemberInviteReq;
import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.service.GroupInvitationService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups-invitation")
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    // 그룹 멤버 초대
    @PostMapping("/{groupId}")
    public ApiResponse<GroupMemberInviteRes> inviteGroupMember(@PathVariable UUID groupId, @RequestBody GroupMemberInviteReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onCreateSuccess(groupInvitationService.inviteGroupMember(groupId, req, user));
    }

    // 그룹원 초대 수락
    @PatchMapping("/{groupInvitationId}/accept")
    public ApiResponse<String> acceptInvitation(@PathVariable UUID groupInvitationId, @AuthenticationPrincipal CustomUserDetails user) {
        groupInvitationService.acceptInvitation(groupInvitationId, user);
        return ApiResponse.onSuccess("그룹 초대를 수락하였습니다");
    }

    // 그룹원 초대 거절
    @PatchMapping("/{groupInvitationId}/decline")
    public ApiResponse<String> declineInvitation(@PathVariable UUID groupInvitationId, @AuthenticationPrincipal CustomUserDetails user) {
        groupInvitationService.declineInvitation(groupInvitationId, user);
        return ApiResponse.onSuccess("그룹 초대를 거절하였습니다");
    }
}
