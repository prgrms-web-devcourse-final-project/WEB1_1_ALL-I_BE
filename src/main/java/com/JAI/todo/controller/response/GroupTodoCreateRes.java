package com.JAI.todo.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupTodoCreateRes {
    UUID groupTodoId;
    List<UUID> groupTodoMappingId;
}
