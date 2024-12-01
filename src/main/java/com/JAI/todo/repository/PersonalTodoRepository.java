package com.JAI.todo.repository;

import com.JAI.todo.domain.PersonalTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalTodoRepository extends JpaRepository<PersonalTodo, UUID> {
    List<PersonalTodo> findAllByDateBetweenAndUser_UserId(LocalDate startDate, LocalDate endDate, UUID userId);

    List<PersonalTodo> findByDate(LocalDate date);
}
