package com.JAI.alarm.repository;

import com.JAI.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {
    // 사용자에게 보낼 알림 조회
    @Query("SELECT a FROM Alarm a WHERE a.scheduledTime <= :standardTime AND a.isSent = false")
    List<Alarm> findPendingAlarmsBetween(LocalDateTime standardTime);

    // 개인 일정 알림 변경 시 사용
    Alarm findByPersonalEvent_PersonalEventId(UUID personalEventId);
}
