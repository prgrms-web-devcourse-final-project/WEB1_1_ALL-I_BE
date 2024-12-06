package com.JAI.group.service.response;

import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.domain.InvitationStatus;
import com.JAI.user.service.dto.UserDTO;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupInvitationForAlarmDTO {
    private UUID groupInvitationId;

    private InvitationStatus status;

    private GroupListRes group;

    private UserDTO user;
}
