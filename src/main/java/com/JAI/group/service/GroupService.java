package com.JAI.group.service;

import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.request.GroupUpdateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.controller.response.GroupUpdateRes;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupCreateRes createGroup(GroupCreateReq req, CustomUserDetails user);
    List<GroupListRes> getGroupList(CustomUserDetails user);
    GroupUpdateRes updateGroupInfo(UUID groupId, GroupUpdateReq req, CustomUserDetails user);
    void deleteGroup(UUID groupId, CustomUserDetails user);
    public GroupListRes getGroupById(UUID groupId);
}
