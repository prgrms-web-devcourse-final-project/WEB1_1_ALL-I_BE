package com.JAI.group.converter;

import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.domain.GroupSetting;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupSettingConverter {
    //DTO -> Entity
    public GroupSetting toGroupSettingEntity(GroupRole role, User user, Group group) {
        GroupSetting groupSetting = GroupSetting.create(
                role,
                user,
                group
        );
        return groupSetting;
    }

    //Entity -> DTO
    // TODO :: 그룹원 조회
    public GroupMemberListRes toGroupMemberListDTO(GroupSetting groupSetting, String nickname) {
        return GroupMemberListRes.builder()
                .groupSettingId(groupSetting.getGroupSettingId())
                .userId(groupSetting.getUser().getUserId())
                .nickname(nickname)
                .role(groupSetting.getRole())
                .build();
    }

    //Service -> Service
    public AddGroupMemberServiceReq toAddGroupMemberServiceReq(Group group, User user, GroupRole groupRole) {
        return AddGroupMemberServiceReq.builder()
                .groupId(group.getGroupId())
                .userId(user.getUserId())
                .role(groupRole)
                .build();
    }
}
