package com.JAI.todo.controller.request;

import com.JAI.category.domain.Category;
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

    String title;

    UUID categoryId;

    LocalTime startTime;

    LocalDate date;
}
