package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.alarm.domain.Alarm;
import com.JAI.alarm.domain.AlarmType;
import com.JAI.alarm.exception.AlarmNotFoundException;
import com.JAI.alarm.mapper.AlarmConverter;
import com.JAI.alarm.repository.AlarmRepository;
import com.JAI.event.DTO.GroupEventForAlarmDTO;
import com.JAI.event.DTO.PersonalEventDTO;
import com.JAI.event.mapper.GroupEventConverter;
import com.JAI.event.mapper.PersonalEventConverter;
import com.JAI.event.service.GroupEventMappingService;
import com.JAI.group.converter.GroupInvitationConverter;
import com.JAI.group.service.response.GroupInvitationDTO;
import com.JAI.group.service.response.GroupInvitationForAlarmDTO;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final AlarmConverter alarmConverter;
    private final UserService userService;
    private final GroupEventMappingService groupEventMappingService;
    private final PersonalEventConverter personalEventConverter;
    private final GroupEventConverter groupEventConverter;
    private final GroupInvitationConverter groupInvitationConverter;

    public void createPersonalEventAlarm(PersonalEventDTO personalEventDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = Optional.ofNullable(personalEventDTO.getStartTime())
                .map(startTime -> personalEventDTO.getStartDate().atTime(startTime))
                .orElse(personalEventDTO.getStartDate().atTime(LocalTime.now()));

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
    public void createGroupEventAlarm(GroupEventForAlarmDTO groupEventForAlarmDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = Optional.ofNullable(groupEventForAlarmDTO.getStartTime())
                .map(startTime -> groupEventForAlarmDTO.getStartDate().atTime(startTime))
                .orElse(groupEventForAlarmDTO.getStartDate().atTime(LocalTime.now()));

        groupEventForAlarmDTO.getAssignedUserIds().forEach(assignedUserId -> {
            // 알림 생성 후 저장
            Alarm alarm = Alarm.builder()
                    .type(AlarmType.EVENT)
                    .scheduledTime(scheduledTime)
                    .description(groupEventConverter.GroupEventForAlarmDTOTogroupEventResDTO(groupEventForAlarmDTO).toString())
                    .user(userService.getUserById(assignedUserId))
                    .groupEventMapping(groupEventMappingService.findById(
                            groupEventForAlarmDTO.getGroupEventId(),
                            groupEventForAlarmDTO.getGroup().getGroupId()
                            , assignedUserId))
                    .build();

            alarmRepository.save(alarm);
        });
    }

    @Override
    @Transactional
    public void createGroupInvitationAlarm(GroupInvitationDTO groupInvitationDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = LocalDateTime.now();

        // 알림 생성 후 저장
        Alarm alarm = Alarm.builder()
                .type(AlarmType.INVITATION)
                .scheduledTime(scheduledTime)
                .description(groupInvitationConverter
                        .toGroupInvitationForAlarmDTO(groupInvitationDTO)
                        .toString())
                .user(groupInvitationDTO.getUser())
                .groupInvitation(groupInvitationConverter
                        .toGroupInvitationEntity(
                                groupInvitationDTO.getGroup(),
                                groupInvitationDTO.getUser()))
                .build();

        alarmRepository.save(alarm);
    }

    @Override
    public List<AlarmResDTO> getAlarm(UUID userId) {
        return alarmRepository.findByUser_UserId(userId).stream()
                .map(alarm -> {
                    AlarmResDTO alarmResDTO = alarmConverter.alarmToAlarmResDTO(alarm);
                    markAlarmAsRead(alarmResDTO);
                    return alarmResDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updatePersonalEventAlarm(PersonalEventDTO personalEventDTO) {
        // 저장된 알림
        Alarm existedAlarm = alarmRepository.findByPersonalEvent_PersonalEventId(personalEventDTO.getPersonalEventId());

        // 개인 일정에 해당하는 알림이 없는 경우 예외 처리
        if (existedAlarm == null) {
            throw new AlarmNotFoundException("해당 일정에 관련된 알림은 없습니다.");
        }

        // 시작 시간 없는 경우 현재 시간으로 설정
        LocalDateTime scheduledTime = Optional.ofNullable(personalEventDTO.getStartTime())
                .map(startTime -> personalEventDTO.getStartDate().atTime(startTime))
                .orElse(personalEventDTO.getStartDate().atTime(LocalTime.now()));

        // 변경된 알림 생성 후 저장
        Alarm updatedAlarm = Alarm.builder()
                .alarmId(existedAlarm.getAlarmId())
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

    @Override
    public void updateGroupEventAlarm(GroupEventForAlarmDTO groupEventForAlarmDTO) {
        groupEventForAlarmDTO.getAssignedUserIds().forEach(assignedUserId -> {
            // 저장된 알림
            Alarm existedAlarm = alarmRepository.findByGroupEventById(
                    groupEventForAlarmDTO.getGroupEventId(),
                    groupEventForAlarmDTO.getGroup().getGroupId(),
                    assignedUserId);

            // 개인 일정에 해당하는 알림이 없는 경우 예외 처리
            if (existedAlarm == null) {
                throw new AlarmNotFoundException("해당 일정에 관련된 알림은 없습니다.");
            }

            // 시작 시간 없는 경우 현재 시간으로 설정
            LocalDateTime scheduledTime = Optional.ofNullable(groupEventForAlarmDTO.getStartTime())
                    .map(startTime -> groupEventForAlarmDTO.getStartDate().atTime(startTime))
                    .orElse(groupEventForAlarmDTO.getStartDate().atTime(LocalTime.now()));

            // 변경된 알림 생성 후 저장
            Alarm updatedAlarm = Alarm.builder()
                    .alarmId(existedAlarm.getAlarmId())
                    .type(existedAlarm.getType())
                    .description(groupEventConverter.GroupEventForAlarmDTOTogroupEventResDTO(groupEventForAlarmDTO).toString())
                    .scheduledTime(scheduledTime)
                    .createdAt(existedAlarm.getCreatedAt())
                    .user(existedAlarm.getUser())
                    .groupEventMapping(groupEventMappingService.findById(groupEventForAlarmDTO.getGroupEventId(),
                            groupEventForAlarmDTO.getGroup().getGroupId()
                            , assignedUserId))
                    .build();

            alarmRepository.save(updatedAlarm);
        });
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    protected void deleteAlarm() {
        log.info("Checking alarms need to be deleted...");

        // 읽은지 7일이 지난 알림 삭제
        LocalDateTime standardTime = LocalDateTime.now().minusDays(7);

        alarmRepository.deleteAlarmNeedToBeDelete(standardTime);

        log.info("Alarms need to be deleted.");
    }

    @Override
    public List<AlarmResDTO> findPendingAlarms(LocalDateTime start, LocalDateTime end) {
        // 그룹 초대 알람 조회
        List<Alarm> invitationAlarms = alarmRepository.findPendingInvitationAlarms(end);

        // 일정 알람 조회
        List<Alarm> personalEventAlarms = alarmRepository.findPendingEventAlarmsBetween(start, end);

        return Stream.concat(
                        invitationAlarms.stream(),
                        personalEventAlarms.stream()
                )
                .map(alarmConverter::alarmToAlarmResDTO)
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

    // 보낸 알림  칼럼 변경
    private void markAlarmAsRead(AlarmResDTO alarmResDTO) {
        Optional<Alarm> targetAlarm = alarmRepository.findById(alarmResDTO.getAlarmId());
        LocalDateTime now = LocalDateTime.now();

        targetAlarm.ifPresent(alarm -> {
            alarm.markAsSent();
            alarm.markAsRead();
            alarm.updateReadTime(now);
            alarmRepository.save(alarm);
        });
    }
}
