package com.JAI.todo.repository;

import com.JAI.todo.domain.PersonalTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonalTodoRepository extends JpaRepository<PersonalTodo, UUID> {
}
