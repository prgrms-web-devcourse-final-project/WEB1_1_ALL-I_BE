package com.JAI.todo.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
// TODO :: todo 작업

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalTodoCreateReq {

    String title;

    LocalDate date;

    LocalTime startTime;

    UUID categoryId;
}
