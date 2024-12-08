package com.JAI.group.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.request.GroupMemberInviteReq;
import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.service.GroupInvitationService;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    @Operation(summary = "그룹 초대 요청", description = "그룹 초대 요청 API")
    @Parameter(name = "nickname", description = "초대하고 싶은 사용자의 닉네임(NOT NULL)", example = "주먹왕 랄프")
    public ApiResponse<GroupMemberInviteRes> inviteGroupMember(@PathVariable UUID groupId, @RequestBody GroupMemberInviteReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onCreateSuccess(groupInvitationService.inviteGroupMember(groupId, req, user));
    }

    // 그룹원 초대 수락
    @PatchMapping("/{groupInvitationId}/accept")
    @Operation(summary = "그룹 초대 수락 요청", description = "그룹 초대 수락 요청 API")
    public ApiResponse<String> acceptInvitation(@PathVariable UUID groupInvitationId, @AuthenticationPrincipal CustomUserDetails user) {
        groupInvitationService.acceptInvitation(groupInvitationId, user);
        return ApiResponse.onSuccess("그룹 초대를 수락하였습니다");
    }

    // 그룹원 초대 거절
    @PatchMapping("/{groupInvitationId}/decline")
    @Operation(summary = "그룹 초대 거절 요청", description = "그룹 초대 거절 요청 API")
    public ApiResponse<String> declineInvitation(@PathVariable UUID groupInvitationId, @AuthenticationPrincipal CustomUserDetails user) {
        groupInvitationService.declineInvitation(groupInvitationId, user);
        return ApiResponse.onSuccess("그룹 초대를 거절하였습니다");
    }
}
