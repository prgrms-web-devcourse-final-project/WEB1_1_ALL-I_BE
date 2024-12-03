package com.JAI.group.service;

import com.JAI.group.controller.request.GroupMemberInviteReq;
import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.UUID;

// TODO :: 그룹 멤버
public interface GroupInvitationService {
    GroupMemberInviteRes inviteGroupMember(UUID groupId, GroupMemberInviteReq req, CustomUserDetails user);
    void acceptInvitation(UUID groupInvitationId, CustomUserDetails user);
    void declineInvitation(UUID groupInvitationId, CustomUserDetails user);
}
