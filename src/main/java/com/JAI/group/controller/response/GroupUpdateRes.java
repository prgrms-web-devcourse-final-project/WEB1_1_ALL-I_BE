package com.JAI.group.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupUpdateRes {
    UUID groupId;

    String groupName;

    String description;

    String color;
}
