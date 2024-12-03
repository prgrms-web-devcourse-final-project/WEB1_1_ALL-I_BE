package com.JAI.group.converter;

import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.controller.response.GroupUpdateRes;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.Role;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupConverter {
    //DTO -> Entity
    public Group toGroupCreateEntity(GroupCreateReq request) {
        Group group = Group.create(
                request.getGroupName(),
                request.getDescription()
        );
        return group;
    }

    //Entity -> DTO
    public GroupCreateRes toGroupCreateDTO(Group group, User user, UUID categoryId) {
        return GroupCreateRes.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .userId(user.getUserId())
                .categoryId(categoryId)
                .build();
    }

    public GroupListRes toGroupListDTO(Group group) {
        return GroupListRes.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .description(group.getDescription())
                .build();
    }

    public GroupUpdateRes toGroupUpdateDTO(Group group, String color) {
        return GroupUpdateRes.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .description(group.getDescription())
                .color(color)
                .build();
    }
}
