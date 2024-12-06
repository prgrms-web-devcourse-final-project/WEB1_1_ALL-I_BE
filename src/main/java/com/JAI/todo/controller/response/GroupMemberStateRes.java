package com.JAI.todo.controller.response;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class GroupMemberStateRes {
    UUID userId;
    boolean done;
}
