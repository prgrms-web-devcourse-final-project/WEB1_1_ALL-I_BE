package com.JAI.alarm.repository;

import com.JAI.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    Alarm findByPersonalEvent_PersonalEventId(UUID personalEventId);

    List<Alarm> findByUser_UserId(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Alarm a WHERE a.readTime < :standardTime")
    void deleteAlarmNeedToBeDelete(LocalDateTime standardTime);
}
