package com.JAI.todo.controller.request;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PersonalTodoStateReq {
    boolean state;
}
