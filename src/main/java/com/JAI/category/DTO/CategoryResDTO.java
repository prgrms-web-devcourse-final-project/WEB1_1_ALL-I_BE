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
    @NotNull(message = "category id must not be null")
    private UUID categoryId;

    @NotNull(message = "name must not be null")
    private String name;

    @NotNull(message = "color must not be null")
    private String color;

    @NotNull(message = "user id must not be null")
    private UUID userId;

    private UUID groupId;
}
