package com.JAI.category.DTO;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class GroupCategoryResDTO {
    private UUID categoryId;

    private String name;

    private String color;

    private UUID leaderUserId;

    private UUID groupId;
}
