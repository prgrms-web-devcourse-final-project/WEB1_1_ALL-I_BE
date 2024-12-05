package com.JAI.todo.service;


import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.response.GroupMemberStateRes;

import java.util.List;
import java.util.UUID;

public interface GroupTodoMappingService {
    List<UUID> createGroupTodoMapping(UUID groupTodoId, List<UUID> userIdList);

    void updateGroupTodoMappingState(GroupTodoStateReq req, UUID groupId, UUID groupTodoId, UUID userId);

    List<GroupMemberStateRes> getMemberStateByGroupTodoId(UUID groupTodoId);

    boolean checkGroupTodoState(UUID groupTodoId);
}
