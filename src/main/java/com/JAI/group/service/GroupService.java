package com.JAI.group.service;

import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.user.service.dto.CustomUserDetails;

public interface GroupService {
    GroupCreateRes createGroup(GroupCreateReq req, CustomUserDetails user);
}
