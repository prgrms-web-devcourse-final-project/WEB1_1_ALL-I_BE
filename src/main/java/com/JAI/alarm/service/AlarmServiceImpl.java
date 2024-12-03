package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.alarm.domain.Alarm;
import com.JAI.alarm.domain.AlarmType;
import com.JAI.alarm.exception.AlarmNotFoundException;
import com.JAI.alarm.mapper.AlarmConverter;
import com.JAI.alarm.repository.AlarmRepository;
import com.JAI.event.DTO.PersonalEventDTO;
import com.JAI.event.mapper.PersonalEventConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final AlarmConverter alarmConverter;
    private final PersonalEventConverter personalEventConverter;

    // 추후 오버로딩 진행
    public void createAlarm(PersonalEventDTO personalEventDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = Optional.ofNullable(personalEventDTO.getStartTime())
                .map(startTime -> personalEventDTO.getStartDate().atTime(startTime))
                .orElse(LocalDateTime.now());

        // 알림 생성 후 저장
        Alarm alarm = Alarm.builder()
                .type(AlarmType.EVENT)
                .scheduledTime(scheduledTime)
                .description(personalEventConverter
                        .personalEventDTOToPersonalEventResDTO(personalEventDTO)
                        .toString())
                .user(personalEventDTO.getUser())
                .personalEvent(personalEventConverter
                        .personalEventDTOToPersonalEvent(personalEventDTO))
                .build();

        alarmRepository.save(alarm);
    }

    @Override
    public void getAlarm(UUID alarmId, SseEmitter emitter) {

    }

    // 추후 오버로딩 진행
    @Override
    public void updateAlarm(PersonalEventDTO personalEventDTO) {
        // 저장된 알림
        Alarm existedAlarm = alarmRepository.findByPersonalEvent_PersonalEventId(personalEventDTO.getPersonalEventId());

        // 개인 일정에 해당하는 알림이 없는 경우 예외 처리
        if (existedAlarm == null) {
            throw new AlarmNotFoundException("해당 일정에 관련된 알림은 없습니다.");
        }

        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = Optional.ofNullable(personalEventDTO.getStartTime())
                .map(startTime -> personalEventDTO.getStartDate().atTime(startTime))
                .orElse(LocalDateTime.now());

        // 변경된 알림 생성 후 저장
        Alarm updatedAlarm = Alarm.builder()
                .type(existedAlarm.getType())
                .description(personalEventConverter
                        .personalEventDTOToPersonalEventResDTO(personalEventDTO)
                        .toString())
                .scheduledTime(scheduledTime)
                .createdAt(existedAlarm.getCreatedAt())
                .user(existedAlarm.getUser())
                .personalEvent(personalEventConverter
                        .personalEventDTOToPersonalEvent(personalEventDTO))
                .build();

        alarmRepository.save(updatedAlarm);
    }

    // 보내야 할 알림 찾기
    @Override
    public List<AlarmResDTO> findPendingAlarms(LocalDateTime time) {
        return alarmRepository.findPendingAlarmsBetween(time)
                .stream()
                .map(alarmConverter::alarmResDTOToAlarm)
                .collect(Collectors.toList());
    }

    // 보낸 알림 isSent 칼럼 변경
    @Override
    public void markAlarmAsSent(AlarmResDTO alarmResDTO) {
        Optional<Alarm> targetAlarm = alarmRepository.findById(alarmResDTO.getAlarmId());
        targetAlarm.ifPresent(alarm -> {
            alarm.markAsSent();
            alarmRepository.save(alarm);
        });
    }
}
