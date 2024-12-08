package com.JAI.todo.controller.request;

import com.JAI.category.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalTodoUpdateReq {
    @NotBlank(message = "내용은 필수 입니다.")
    String title;

    @NotNull(message = "카테고리 선택은 필수 입니다.")
    UUID categoryId;

    LocalTime startTime;
    @NotNull(message = "날짜는 필수 입니다.")
    LocalDate date;
}
