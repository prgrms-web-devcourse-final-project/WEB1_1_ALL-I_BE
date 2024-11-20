package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupTodoRepository extends JpaRepository<GroupTodo, UUID> {
}
