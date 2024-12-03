package com.JAI.group.controller.response;

import com.JAI.group.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
// TODO :: 그룹 멤버
// TODO :: 그룹 멤버 초대
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupMemberInviteRes {
    UUID groupInvitationId;

    String nickname;

    UUID groupId;

    Status stats;
}
