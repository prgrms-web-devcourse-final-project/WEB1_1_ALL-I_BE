package com.JAI.category.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class CategoryResDTO {
    private UUID categoryId;

    private String name;

    private String color;

    private UUID userId;

    private UUID groupId;
}
