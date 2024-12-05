package com.JAI.todo.repository;

import com.JAI.todo.controller.response.GroupMemberStateRes;
import com.JAI.todo.domain.GroupTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupTodoMappingRepository extends JpaRepository<GroupTodoMapping, UUID> {

    Optional<GroupTodoMapping> findByGroupSetting_GroupSettingIdAndGroupTodo_GroupTodoId(UUID groupSettingId, UUID groupTodoId);

    @Query("SELECT COUNT(gtm) FROM GroupTodoMapping gtm WHERE gtm.groupTodo.groupTodoId = :groupTodoId")
    int countByGroupTodoId(@Param("groupTodoId") UUID groupTodoId);

    @Query("SELECT COUNT(gtm) FROM GroupTodoMapping gtm " +
            "WHERE gtm.groupTodo.groupTodoId = :groupTodoId AND gtm.done = true")
    int countDoneByGroupTodoId(@Param("groupTodoId") UUID groupTodoId);

    @Query("SELECT gtm FROM GroupTodoMapping gtm " +
            "JOIN FETCH gtm.groupSetting gs " +
            "WHERE gtm.groupTodo.groupTodoId = :groupTodoId")
    List<GroupTodoMapping> findByGroupTodoId(@Param("groupTodoId") UUID groupTodoId);

}
