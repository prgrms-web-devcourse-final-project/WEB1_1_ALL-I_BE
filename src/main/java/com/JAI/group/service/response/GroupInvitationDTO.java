package com.JAI.group.service.response;

import com.JAI.group.domain.Group;
import com.JAI.group.domain.InvitationStatus;
import com.JAI.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitationDTO {
    private UUID groupInvitationId;

    private InvitationStatus status;

    private Group group;

    private User user;
}
