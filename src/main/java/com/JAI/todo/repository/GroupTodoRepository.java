package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupTodoRepository extends JpaRepository<GroupTodo, UUID> {
}
