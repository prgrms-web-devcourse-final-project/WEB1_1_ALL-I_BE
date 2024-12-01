package com.JAI.group.service;

import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface GroupSettigService {
    void addGroupMember(AddGroupMemberServiceReq req);
    List<UUID> getGroupIdList(User user);
}
