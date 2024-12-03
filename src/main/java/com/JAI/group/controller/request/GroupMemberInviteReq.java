package com.JAI.group.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO :: 그룹 멤버 초대
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupMemberInviteReq {
    @NotBlank(message = "닉네임은 필수 입니다.")
    String nickname;
}
