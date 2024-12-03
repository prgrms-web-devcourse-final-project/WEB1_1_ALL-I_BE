package com.JAI.group.service;

import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface GroupSettingService {
    void addGroupMember(AddGroupMemberServiceReq req);
    List<UUID> getGroupIdList(UUID userId);
    GroupRole findGroupMemberRole(UUID groupId, UUID userId);
    void findGroupMemberExisted(UUID groupId, UUID userId);
    List<GroupMemberListRes> getGroupMembers(UUID groupId);

    void quitGroupMember(UUID groupSettingId, CustomUserDetails user);
    void ejectionGroupMember(UUID groupSettingId, CustomUserDetails user);
}
