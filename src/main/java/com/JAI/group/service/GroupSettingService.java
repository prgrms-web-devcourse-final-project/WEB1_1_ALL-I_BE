package com.JAI.group.service;

import com.JAI.group.domain.GroupRole;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;

import java.util.List;
import java.util.UUID;

public interface GroupSettingService {
    void addGroupMember(AddGroupMemberServiceReq req);
    List<UUID> getGroupIdList(UUID userId);
    GroupRole findGroupMemberRole(UUID groupId, UUID userId);
}
