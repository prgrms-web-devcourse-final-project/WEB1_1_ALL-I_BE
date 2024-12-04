package com.JAI.category.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class CategoryCreateReqDTO {
    @NotNull(message = "name must not be null")
    private String name;

    @NotNull(message = "color must not be null")
    private String color;
}
