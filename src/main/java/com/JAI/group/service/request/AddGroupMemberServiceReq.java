package com.JAI.group.service.request;

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
public class AddGroupMemberServiceReq {

    UUID groupId;

    UUID userId;

    GroupRole role;
}
