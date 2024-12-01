package com.JAI.group.service;

import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.user.service.dto.CustomUserDetails;

import java.util.List;

public interface GroupService {
    GroupCreateRes createGroup(GroupCreateReq req, CustomUserDetails user);
    List<GroupListRes> getGroupList(CustomUserDetails user);
}
