package com.JAI.todo.controller.request;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalTodoStateReq {
    boolean state;
}
