package com.JAI.group.controller.response;

import com.JAI.group.domain.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupMemberListRes {
    UUID groupSettingId;
    UUID userId;
    String nickname;
    GroupRole role;
}
