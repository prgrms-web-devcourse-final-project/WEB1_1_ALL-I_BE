package com.JAI.alarm.repository;

import com.JAI.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {
}
