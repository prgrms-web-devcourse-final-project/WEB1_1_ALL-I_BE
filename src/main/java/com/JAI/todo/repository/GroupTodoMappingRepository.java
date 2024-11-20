package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupTodoMappingRepository extends JpaRepository<GroupTodoMapping, UUID> {
}
