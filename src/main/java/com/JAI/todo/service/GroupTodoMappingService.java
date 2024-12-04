package com.JAI.todo.service;


import java.util.List;
import java.util.UUID;

public interface GroupTodoMappingService {
    List<UUID> createGroupTodoMapping(UUID groupTodoId, List<UUID> userIdList);
}
