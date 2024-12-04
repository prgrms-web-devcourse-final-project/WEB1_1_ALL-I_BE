package com.JAI.category.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class CategoryUpdateReqDTO {
    @NotNull(message = "category id must not be null")
    private UUID categoryId;

    private String name;

    private String color;
}