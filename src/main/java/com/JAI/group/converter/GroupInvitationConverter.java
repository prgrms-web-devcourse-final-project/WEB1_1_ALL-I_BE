package com.JAI.group.converter;

import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupInvitation;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// TODO :: 그룹 멤버
@Component
@RequiredArgsConstructor
public class GroupInvitationConverter {
    //DTO -> Entity
    // TODO :: 그룹 멤버 초대
    public GroupInvitation toGroupInvitationEntity(Group group, User user) {
        return GroupInvitation.create(
                group,
                user
        );
    }

    //Entity -> DTO
    public GroupMemberInviteRes toGroupMemberInviteDTO(GroupInvitation groupInvitation, String nickname) {
        return GroupMemberInviteRes.builder()
                .groupInvitationId(groupInvitation.getGroupInvitationId())
                .nickname(nickname)
                .groupId(groupInvitation.getGroup().getGroupId())
                .stats(groupInvitation.getStatus())
                .build();
    }
}
