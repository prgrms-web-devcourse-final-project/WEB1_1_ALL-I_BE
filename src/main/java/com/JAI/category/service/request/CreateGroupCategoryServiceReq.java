package com.JAI.category.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateGroupCategoryServiceReq {
    String name;
    String color;
    UUID userId;
    UUID groupId;
}
