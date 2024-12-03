package com.JAI.alarm.controller;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.alarm.service.AlarmNotificationService;
import com.JAI.alarm.service.AlarmService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmNotificationService alarmNotificationService;
    private final AlarmService alarmService;

    // 실시간으로 알림 조회 (단 건으로 여러 번)
    @GetMapping(value = "/connect")
    public SseEmitter getAlarmsRealTime(@AuthenticationPrincipal CustomUserDetails user) {
        return alarmNotificationService.getRealTimeAlarms(user.getUserId());
    }

    // 사용자가 받은 알림 전체 조회 (여태 받은 알림 한 번에)
    @GetMapping
    public List<AlarmResDTO> getAlarms(@AuthenticationPrincipal CustomUserDetails user) {
        return alarmService.getAlarm(user.getUserId());
    }
}
