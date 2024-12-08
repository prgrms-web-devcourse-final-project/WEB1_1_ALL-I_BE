package com.JAI.todo.repository;

import com.JAI.todo.domain.GroupTodo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupTodoRepository extends JpaRepository<GroupTodo, UUID> {

    List<GroupTodo> findByGroup_GroupIdAndDateBetween(UUID groupId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT ge " +
            "FROM GroupTodo ge " +
            "JOIN GroupTodoMapping gem ON ge.groupTodoId = gem.groupTodo.groupTodoId " +
            "JOIN GroupSetting gs ON gs.groupSettingId = gem.groupSetting.groupSettingId " +
            "WHERE gs.user.userId = :someoneUserId " +
            "AND gs.group.groupId = :groupId " +
            "AND ge.date BETWEEN :startDate AND :endDate")
    List<GroupTodo> findByGroupIdAndUserIdAndDateBetween(@Param("groupId") UUID groupId,
                                                         @Param("someoneUserId") UUID someoneUserId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT ge " +
            "FROM GroupTodo ge " +
            "JOIN GroupTodoMapping gem ON ge.groupTodoId = gem.groupTodo.groupTodoId " +
            "JOIN GroupSetting gs ON gs.groupSettingId = gem.groupSetting.groupSettingId " +
            "WHERE gs.user.userId = :userId " +
            "AND ge.date BETWEEN :startDate AND :endDate")
    List<GroupTodo> findByUserIdAndDateBetween(@Param("userId") UUID userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
