package com.JAI.alarm.repository;

import com.JAI.alarm.domain.Alarm;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {
    // 사용자에게 보낼 일정 알림 조회
    @Query("SELECT a FROM Alarm a WHERE a.scheduledTime BETWEEN :start AND :end AND a.isSent = false AND a.type = com.JAI.alarm.domain.AlarmType.EVENT")
    List<Alarm> findPendingEventAlarmsBetween(LocalDateTime start, LocalDateTime end);

    // 사용자에게 보낼 초대 알림 조회
    @Query("SELECT a FROM Alarm a WHERE a.scheduledTime <= :standardTime AND a.isSent = false AND a.type = com.JAI.alarm.domain.AlarmType.INVITATION")
    List<Alarm> findPendingInvitationAlarms(LocalDateTime standardTime);


    @Query("SELECT a FROM Alarm a WHERE a.personalEvent.personalEventId = :personalEventId")
    Alarm findByPersonalEvent_PersonalEventId(UUID personalEventId);

    @Query("SELECT a FROM Alarm a WHERE a.user.userId = :userId AND a.scheduledTime <= :now")
    List<Alarm> findByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM Alarm a WHERE a.readTime <= :standardTime AND a.readTime IS NOT NULL")
    void deleteAlarmNeedToBeDelete(LocalDateTime standardTime);

    @Query("SELECT a " +
            "FROM Alarm a " +
            "JOIN GroupEventMapping gem ON gem.groupEventMappingId = a.groupEventMapping.groupEventMappingId " +
            "JOIN GroupSetting gs ON gs.groupSettingId = gem.groupSetting.groupSettingId " +
            "WHERE gs.group.groupId = :groupId " +
            "AND gs.user.userId = :userId " +
            "AND gem.groupEvent.groupEventId = :groupEventId")
    Alarm findByGroupEventById(UUID groupEventId, UUID groupId, UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Alarm a WHERE a.groupInvitation.groupInvitationId = :groupInvitationId")
    void deleteByGroupInvitationId(@Param("groupInvitationId") UUID groupInvitationId);
}
