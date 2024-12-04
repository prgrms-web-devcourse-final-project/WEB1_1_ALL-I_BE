package com.JAI.group.service.response;

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
public class GroupSettingRes {
    UUID groupSettingId;

    GroupRole role;
}
