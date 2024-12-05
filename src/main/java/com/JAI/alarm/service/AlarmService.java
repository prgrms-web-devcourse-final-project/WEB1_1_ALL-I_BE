package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.event.DTO.GroupEventForAlarmDTO;
import com.JAI.event.DTO.PersonalEventDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlarmService {
    public void createPersonalEventAlarm(PersonalEventDTO personalEventDTO);

    public void createGroupEventAlarm(GroupEventForAlarmDTO groupEventForAlarmDTO);

    public void updatePersonalEventAlarm(PersonalEventDTO personalEventDTO);

    public void updateGroupEventAlarm(GroupEventForAlarmDTO groupEventForAlarmDTO);

    public List<AlarmResDTO> getAlarm(UUID userId);

    public List<AlarmResDTO> findPendingAlarms(LocalDateTime start, LocalDateTime end);

    public void markAlarmAsSent(AlarmResDTO alarmResDTO);
}
