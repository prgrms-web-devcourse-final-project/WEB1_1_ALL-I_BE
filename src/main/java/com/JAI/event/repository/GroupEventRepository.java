package com.JAI.event.repository;

import com.JAI.event.domain.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GroupEventRepository extends JpaRepository<GroupEvent, UUID> {
    List<GroupEvent> findByGroup_GroupIdAndStartDateBetween(UUID groupId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT ge " +
            "FROM GroupEvent ge " +
            "JOIN GroupEventMapping gem ON ge.groupEventId = gem.groupEvent.groupEventId " +
            "JOIN GroupSetting gs ON gs.groupSettingId = gem.groupSetting.groupSettingId " +
            "WHERE gs.user.userId = :someoneUserId " +
            "AND gs.group.groupId = :groupId " +
            "AND ge.startDate BETWEEN :startDate AND :endDate")
    List<GroupEvent> findByGroupIdAndUserIdAndStartDateBetween(UUID groupId, UUID someoneUserId, LocalDate startDate, LocalDate endDate);
}
