package com.JAI.event.service;

import com.JAI.alarm.service.AlarmService;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.GroupEventForAlarmDTO;
import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.request.GroupEventUpdateReqDTO;
import com.JAI.event.DTO.response.*;
import com.JAI.event.domain.GroupEvent;
import com.JAI.event.domain.GroupEventMapping;
import com.JAI.event.exception.GroupEventNotFoundException;
import com.JAI.event.mapper.GroupEventConverter;
import com.JAI.event.repository.GroupEventMappingRepository;
import com.JAI.event.repository.GroupEventRepository;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.service.GroupService;
import com.JAI.group.service.GroupSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupEventServiceImpl implements GroupEventService {
    private final GroupEventRepository groupEventRepository;
    private final GroupEventMappingRepository groupEventMappingRepository;
    private final GroupSettingService groupSettingService;
    private final CategoryService categoryService;
    private final GroupService groupService;
    private final AlarmService alarmService;
    private final GroupEventConverter groupEventConverter;

    // 특정 그룹의 특정 달 내에 모든 그룹 일정
    @Override
    public GetGroupEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month) {
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다.");
        }

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);

        // groupCategory를 리스트로 생성 / 반환값 통일을 위함
        List<GroupCategoryResDTO> groupCategoryList = new ArrayList<>();
        groupCategoryList.add(groupCategory);

        // group을 리스트로 생성 / 반환값 통일을 위함
        List<GroupListRes> groupList = new ArrayList<>();
        groupList.add(group);

        // 그룹 일정 리스트
        List<GroupEventResDTO> groupDTOs = groupEventRepository.findByGroup_GroupIdAndStartDateBetween(groupId, startDate, endDate)
                .stream()
                .map(groupEvent -> {
                    // groupEvent를 DTO로 변환
                    GroupEventResDTO groupDTO = groupEventConverter.groupEventToGroupEventResDTO(groupEvent);
                    groupDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(groupDTO.getGroupEventId()));

                    return groupDTO;
                })
                .toList();

        return new GetGroupEventResDTO(groupList, groupCategoryList, groupDTOs);
    }

    @Override
    public GetGroupEventResDTO getGroupSomeOneEvents(UUID groupId, UUID someoneUserId, UUID userId, String year, String month) {
        if (!groupSettingService.isGroupMemberExisted(groupId, someoneUserId)) {
            throw new GroupNotFoundException("찾고자 하는 사용자는 해당 그룹에 속하지 않습니다.");
        }

        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다.");
        }

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);

        // groupCategory를 리스트로 생성 / 반환값 통일을 위함
        List<GroupCategoryResDTO> groupCategoryList = new ArrayList<>();
        groupCategoryList.add(groupCategory);

        // group을 리스트로 생성 / 반환값 통일을 위함
        List<GroupListRes> groupList = new ArrayList<>();
        groupList.add(group);

        List<GroupEventResDTO> groupEventDTOs = groupEventRepository.findByGroupIdAndUserIdAndStartDateBetween(groupId, someoneUserId, startDate, endDate)
                .stream()
                .map(groupEvent -> {
                    // groupEvent를 DTO로 변환
                    GroupEventResDTO groupDTO = groupEventConverter.groupEventToGroupEventResDTO(groupEvent);
                    groupDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(groupDTO.getGroupEventId()));

                    return groupDTO;
                })
                .toList();

        return new GetGroupEventResDTO(groupList, groupCategoryList, groupEventDTOs);
    }

    @Override
    public GetGroupEventResDTO getGroupMyEvents(UUID userId, String year, String month) {
        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = groupService.getGroupByUserId(userId);
        List<GroupCategoryResDTO> categoryResDTOs = categoryService.getOnlyGroupCategoryByUserId(userId);
        List<GroupEventResDTO> groupEventDTOs = groupEventRepository.findByUserIdAndStartDateBetween(userId, startDate, endDate)
                .stream()
                .map(groupEvent -> {
                    // groupEvent를 DTO로 변환
                    GroupEventResDTO groupDTO = groupEventConverter.groupEventToGroupEventResDTO(groupEvent);
                    groupDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(groupDTO.getGroupEventId()));

                    return groupDTO;
                })
                .toList();

        return new GetGroupEventResDTO(groupDTOs, categoryResDTOs, groupEventDTOs);
    }

    @Override
    public GroupEventResDTO createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId) {
        // 사용자가 해당 그룹에 속하지 않는 에러 처리
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException(userId + "사용자는 해당 그룹에 속하지 않습니다.");
        }

        GroupEvent groupEvent = groupEventConverter.groupEventCreateReqDTOToGroupEvent(groupEventCreateReqDTO);
        groupEvent.updateGroup(groupService.findGroupEntityById(groupId));

        GroupEvent savedGroupEvent = groupEventRepository.save(groupEvent);
        groupEventCreateReqDTO.getAssignedMemberList().forEach(assignedMemberUserId -> {
            GroupEventMapping groupEventMapping = GroupEventMapping.builder()
                    .groupEvent(savedGroupEvent)
                    .groupSetting(groupSettingService.findGroupSettingByGroupIdAndUserId(groupId, assignedMemberUserId))
                    .build();

            groupEventMappingRepository.save(groupEventMapping);
        });

        List<UUID> assignedMemberUserIds = groupSettingService.getGroupEventRelatedUsers(savedGroupEvent.getGroupEventId());

        GroupEventResDTO groupEventResDTO = groupEventConverter.groupEventToGroupEventResDTO(savedGroupEvent);
        groupEventResDTO.updateUserIds(assignedMemberUserIds);

        // 알림 저장
        if (groupEventResDTO.getIsAlarmed()) {
            GroupEventForAlarmDTO groupEventForAlarmDTO = groupEventConverter.groupEventToGroupEventDTO(savedGroupEvent);
            groupEventForAlarmDTO.updateUserIds(assignedMemberUserIds);

            alarmService.createGroupEventAlarm(groupEventForAlarmDTO);
        }

        return groupEventResDTO;
    }

    @Override
    public GroupEventResDTO updateGroupEvent(UUID groupId, UUID groupEventId, GroupEventUpdateReqDTO groupEventUpdateReqDTO, UUID userId) {
        // 사용자가 해당 그룹에 속하지 않는 에러 처리
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException(userId + "사용자는 해당 그룹에 속하지 않습니다.");
        }

        GroupEvent existedGroupEvent = groupEventRepository.findById(groupEventId)
                .orElseThrow(() -> new GroupEventNotFoundException(groupEventId + "인 그룹 일정을 찾을 수 없습니다."));

        GroupEvent updatedGroupEvent = groupEventConverter.groupEventUpdateReqDTOToGroupEvent(groupEventUpdateReqDTO, existedGroupEvent);
        updatedGroupEvent.updateGroup(groupService.findGroupEntityById(groupId));

        groupEventRepository.save(updatedGroupEvent);

        List<UUID> existedAssignedMemberList = groupSettingService.getGroupEventRelatedUsers(updatedGroupEvent.getGroupEventId());

        // 할당된 멤버 변경에 따른 그룹 일정 매핑 변경
        if (groupEventUpdateReqDTO.getAssignedMemberList() != null) {
            List<UUID> needToBeDeleteMember = existedAssignedMemberList.stream()
                    .filter(uuid -> !groupEventUpdateReqDTO.getAssignedMemberList().contains(uuid))
                    .toList();

            List<UUID> needToBeAddMember = groupEventUpdateReqDTO.getAssignedMemberList().stream()
                    .filter(uuid -> !existedAssignedMemberList.contains(uuid))
                    .toList();

            needToBeDeleteMember.forEach(uuid -> {
                groupEventMappingRepository.deleteByGroupSettingId(groupSettingService.findGroupSettingByGroupIdAndUserId(groupId, uuid).getGroupSettingId());
            });

            needToBeAddMember.forEach(uuid -> {
                GroupEventMapping groupEventMapping = GroupEventMapping.builder()
                        .groupEvent(updatedGroupEvent)
                        .groupSetting(groupSettingService.findGroupSettingByGroupIdAndUserId(groupId, uuid))
                        .build();

                groupEventMappingRepository.save(groupEventMapping);
            });
        }

        List<UUID> assignedMemberUserIds = groupSettingService.getGroupEventRelatedUsers(updatedGroupEvent.getGroupEventId());

        GroupEventResDTO groupEventResDTO = groupEventConverter.groupEventToGroupEventResDTO(updatedGroupEvent);
        groupEventResDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(updatedGroupEvent.getGroupEventId()));

        // 알림 저장
        if (groupEventResDTO.getIsAlarmed()) {
            GroupEventForAlarmDTO groupEventForAlarmDTO = groupEventConverter.groupEventToGroupEventDTO(updatedGroupEvent);
            groupEventForAlarmDTO.updateUserIds(assignedMemberUserIds);

            alarmService.updateGroupEventAlarm(groupEventForAlarmDTO);
        }

        return groupEventResDTO;
    }

    @Override
    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId) {
        // 사용자가 해당 그룹에 속하지 않는 에러 처리
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException(userId + "사용자는 해당 그룹에 속하지 않습니다.");
        }

        groupEventRepository.deleteById(groupEventId);
    }
}
