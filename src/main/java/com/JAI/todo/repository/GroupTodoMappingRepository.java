package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupTodoMappingRepository extends JpaRepository<GroupTodoMapping, UUID> {
}
