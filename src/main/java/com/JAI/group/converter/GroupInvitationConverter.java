package com.JAI.group.converter;

import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupInvitation;
import com.JAI.group.service.response.GroupInvitationForAlarmDTO;
import com.JAI.group.service.response.GroupInvitationResDTO;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.UserDTO;
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

    public GroupInvitationForAlarmDTO toGroupInvitationDTO(GroupInvitation groupInvitation, UserDTO sender) {
        return GroupInvitationForAlarmDTO.builder()
                .groupInvitationId(groupInvitation.getGroupInvitationId())
                .status(groupInvitation.getStatus())
                .group(groupInvitation.getGroup())
                .receiver(groupInvitation.getUser())
                .sender(sender)
                .build();
    }

    public GroupInvitationResDTO toGroupInvitationResDTO(GroupInvitationForAlarmDTO groupInvitationForAlarmDTO) {
        return GroupInvitationResDTO.builder()
                .groupInvitationId(groupInvitationForAlarmDTO.getGroupInvitationId())
                .status(groupInvitationForAlarmDTO.getStatus())
                .sender(groupInvitationForAlarmDTO.getSender())
                .build();
    }

    public GroupInvitation toGroupInvitation(GroupInvitationForAlarmDTO groupInvitationForAlarmDTO) {
        return GroupInvitation.builder()
                .groupInvitationId(groupInvitationForAlarmDTO.getGroupInvitationId())
                .status(groupInvitationForAlarmDTO.getStatus())
                .group(groupInvitationForAlarmDTO.getGroup())
                .user(groupInvitationForAlarmDTO.getReceiver())
                .build();
    }
}
