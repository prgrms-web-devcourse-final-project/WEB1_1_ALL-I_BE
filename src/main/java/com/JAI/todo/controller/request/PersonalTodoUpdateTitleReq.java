package com.JAI.todo.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalTodoUpdateTitleReq {
    @NotBlank(message = "내용은 필수 입니다.")
    String title;
}
