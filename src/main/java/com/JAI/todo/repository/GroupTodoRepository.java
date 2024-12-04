package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupTodoRepository extends JpaRepository<GroupTodo, UUID> {

    List<GroupTodo> findByGroup_GroupIdAndDateBetween(UUID groupId, LocalDate startDate, LocalDate endDate);
}
