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
public class GroupInvitationResDTO {
    private UUID groupInvitationId;

    private InvitationStatus status;

    private GroupListRes group;

    private UserDTO receiver;

    private UserDTO sender;

    public void updateGroup (GroupListRes group) {
        if (this.group == null) {
            this.group = group;
        }
    }

    public void updateReceiver (UserDTO receiver) {
        if (this.receiver == null) {
            this.receiver = receiver;
        }
    }
}
