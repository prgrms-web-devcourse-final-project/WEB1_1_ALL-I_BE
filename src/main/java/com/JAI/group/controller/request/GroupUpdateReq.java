package com.JAI.group.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupUpdateReq {
    @NotBlank(message = "그룹 이름은 필수 입니다.")
    String name;

    String description;

    String color;
}
