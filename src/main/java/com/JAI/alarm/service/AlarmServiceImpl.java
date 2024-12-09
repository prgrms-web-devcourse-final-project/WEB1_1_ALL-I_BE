package com.JAI.alarm.service;

import com.JAI.alarm.DTO.AlarmResDTO;
import com.JAI.alarm.domain.Alarm;
import com.JAI.alarm.domain.AlarmType;
import com.JAI.alarm.mapper.AlarmConverter;
import com.JAI.alarm.repository.AlarmRepository;
import com.JAI.event.DTO.GroupEventForAlarmDTO;
import com.JAI.event.DTO.PersonalEventDTO;
import com.JAI.event.mapper.GroupEventConverter;
import com.JAI.event.mapper.PersonalEventConverter;
import com.JAI.event.service.GroupEventMappingService;
import com.JAI.group.converter.GroupConverter;
import com.JAI.group.converter.GroupInvitationConverter;
import com.JAI.group.service.response.GroupInvitationForAlarmDTO;
import com.JAI.group.service.response.GroupInvitationResDTO;
import com.JAI.user.converter.UserConverter;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private final GroupConverter groupConverter;
    private final UserConverter userConverter;

    public void createPersonalEventAlarm(PersonalEventDTO personalEventDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        ZonedDateTime scheduledTime;

        if (personalEventDTO.getStartTime() == null) {
            scheduledTime = personalEventDTO.getStartDate().atTime(LocalTime.now()).atZone(ZoneId.systemDefault());
        } else {
            scheduledTime = personalEventDTO.getStartDate().atTime(personalEventDTO.getStartTime()).atZone(ZoneId.systemDefault());
        }

        // 알림 생성 후 저장
        Alarm alarm = Alarm.builder()
                .type(AlarmType.EVENT)
                .scheduledTime(scheduledTime.toLocalDateTime())
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
        ZonedDateTime scheduledTime;

        if (groupEventForAlarmDTO.getStartTime() == null) {
            scheduledTime = groupEventForAlarmDTO.getStartDate().atTime(LocalTime.now()).atZone(ZoneId.systemDefault());
        } else {
            scheduledTime = groupEventForAlarmDTO.getStartDate().atTime(groupEventForAlarmDTO.getStartTime()).atZone(ZoneId.systemDefault());
        }

        groupEventForAlarmDTO.getAssignedUserIds().forEach(assignedUserId -> {
            // 알림 생성 후 저장
            Alarm alarm = Alarm.builder()
                    .type(AlarmType.EVENT)
                    .scheduledTime(scheduledTime)
                    .description(groupEventConverter.GroupEventForAlarmDTOTogroupEventResDTO(groupEventForAlarmDTO).toString())
                    .user(userService.getUserById(assignedUserId))
                    .groupEventMapping(groupEventMappingService.findById(
                            groupEventForAlarmDTO.getGroupEventId(),
                            groupEventForAlarmDTO.getGroup().getGroupId(),
                            assignedUserId))
                    .build();

            alarmRepository.save(alarm);
        });
    }

    @Override
    @Transactional
    public void createGroupInvitationAlarm(GroupInvitationForAlarmDTO groupInvitationForAlarmDTO) {
        LocalDateTime scheduledTime = LocalDateTime.now();

        GroupInvitationResDTO groupInvitationResDTO = groupInvitationConverter.toGroupInvitationResDTO(groupInvitationForAlarmDTO);
        groupInvitationResDTO.updateGroup(groupConverter.toGroupListDTO(groupInvitationForAlarmDTO.getGroup()));
        groupInvitationResDTO.updateReceiver(userConverter.toUserDTO(groupInvitationForAlarmDTO.getReceiver()));

        // 알림 생성 후 저장
        Alarm alarm = Alarm.builder()
                .type(AlarmType.INVITATION)
                .scheduledTime(scheduledTime)
                .description(groupInvitationResDTO.toString())
                .user(groupInvitationForAlarmDTO.getReceiver())
                .groupInvitation(groupInvitationConverter
                        .toGroupInvitation(groupInvitationForAlarmDTO))
                .build();

        alarmRepository.save(alarm);
    }

    @Override
    public List<AlarmResDTO> getAlarm(UUID userId) {
        ZonedDateTime koreanTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")); // 한국 시간
        ZonedDateTime standardTime = koreanTime.withZoneSameInstant(ZoneId.of("UTC")); // 한국 시간을 UTC로 변환

        return alarmRepository.findByUserId(userId, standardTime.toLocalDateTime()).stream()
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
            createPersonalEventAlarm(personalEventDTO);
        } else {
            // 시작 시간 없는 경우 현재 시간으로 설정
            ZonedDateTime scheduledTime;

            if (personalEventDTO.getStartTime() == null) {
                scheduledTime = personalEventDTO.getStartDate().atTime(LocalTime.now()).atZone(ZoneId.systemDefault());
            } else {
                scheduledTime = personalEventDTO.getStartDate().atTime(personalEventDTO.getStartTime()).atZone(ZoneId.systemDefault());
            }

            // 변경된 알림 생성 후 저장
            Alarm updatedAlarm = Alarm.builder()
                    .alarmId(existedAlarm.getAlarmId())
                    .type(existedAlarm.getType())
                    .description(personalEventConverter
                            .personalEventDTOToPersonalEventResDTO(personalEventDTO)
                            .toString())
                    .scheduledTime(scheduledTime.toLocalDateTime())
                    .createdAt(existedAlarm.getCreatedAt())
                    .user(existedAlarm.getUser())
                    .personalEvent(personalEventConverter
                            .personalEventDTOToPersonalEvent(personalEventDTO))
                    .build();

            alarmRepository.save(updatedAlarm);
        }
    }

    @Override
    public void updateGroupEventAlarm(GroupEventForAlarmDTO groupEventForAlarmDTO) {
        // 시작 시간 없는 경우 현재 시간으로 설정
        ZonedDateTime scheduledTime;

        if (groupEventForAlarmDTO.getStartTime() == null) {
            scheduledTime = groupEventForAlarmDTO.getStartDate().atTime(LocalTime.now()).atZone(ZoneId.systemDefault());
        } else {
            scheduledTime = groupEventForAlarmDTO.getStartDate().atTime(groupEventForAlarmDTO.getStartTime()).atZone(ZoneId.systemDefault());
        }

        groupEventForAlarmDTO.getAssignedUserIds().forEach(assignedUserId -> {
            // 저장된 알림
            Alarm existedAlarm = alarmRepository.findByGroupEventById(
                    groupEventForAlarmDTO.getGroupEventId(),
                    groupEventForAlarmDTO.getGroup().getGroupId(),
                    assignedUserId);

            Alarm updatedAlarm = null;

            // 개인 일정에 해당하는 알림이 없는 경우 예외 처리
            if (existedAlarm == null) {
                updatedAlarm = Alarm.builder()
                        .type(AlarmType.EVENT)
                        .scheduledTime(scheduledTime)
                        .description(groupEventConverter.GroupEventForAlarmDTOTogroupEventResDTO(groupEventForAlarmDTO).toString())
                        .user(userService.getUserById(assignedUserId))
                        .groupEventMapping(groupEventMappingService.findById(
                                groupEventForAlarmDTO.getGroupEventId(),
                                groupEventForAlarmDTO.getGroup().getGroupId(),
                                assignedUserId))
                        .build();
            } else {
                // 변경된 알림 생성 후 저장
                updatedAlarm = Alarm.builder()
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
            }

            alarmRepository.save(updatedAlarm);
        });
    }

    @Scheduled(fixedRate = 60000)
    protected void deleteAlarm() {
        log.info("Checking alarms need to be deleted...");

        // 읽은지 7일이 지난 알림 삭제
        LocalDateTime standardTime = LocalDateTime.now().minusDays(7);

        alarmRepository.deleteAlarmNeedToBeDelete(standardTime);

        log.info("Alarms need to be deleted.");
    }

    @Override
    public List<AlarmResDTO> findPendingAlarms(LocalDateTime standardTime) {
        // 그룹 초대 알람 조회
        List<Alarm> invitationAlarms = alarmRepository.findPendingInvitationAlarms(standardTime);

        // 일정 알람 조회
        List<Alarm> personalEventAlarms = alarmRepository.findPendingEventAlarms(standardTime);

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

    @Override
    public void deleteAlarmByGroupInvitationId(UUID groupInvitationId) {
        alarmRepository.deleteByGroupInvitationId(groupInvitationId);
    }
}
