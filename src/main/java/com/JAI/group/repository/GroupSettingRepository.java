package com.JAI.group.repository;

import com.JAI.group.domain.GroupSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupSettingRepository extends JpaRepository<GroupSetting, UUID> {
    List<GroupSetting> findByUser_UserId(UUID userId);
    Optional<GroupSetting> findByGroup_GroupIdAndUser_UserId(UUID groupId, UUID userId);
    Boolean existsByGroup_GroupIdAndUser_UserId(UUID groupId, UUID userId);

    @Query("SELECT gs FROM GroupSetting gs JOIN FETCH gs.user WHERE gs.group.groupId = :groupId")
    List<GroupSetting> findByGroup_GroupIdWithUser(@Param("groupId") UUID groupId);

    @Query("SELECT gs.user.userId " +
            "FROM GroupSetting gs " +
            "JOIN GroupEventMapping gem ON gs.groupSettingId = gem.groupSetting.groupSettingId " +
            "JOIN GroupEvent ge ON gem.groupEvent.groupEventId = ge.groupEventId " +
            "WHERE ge.groupEventId = :groupEventId")
    List<UUID> findUserIdByGroupEventGroupId(UUID groupEventId);
}
