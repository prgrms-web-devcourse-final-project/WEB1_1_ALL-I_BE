package com.JAI.event.service;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupEventServiceImpl implements GroupEventService {
    private final GroupEventRepository groupEventRepository;
    private final GroupEventMappingRepository groupEventMappingRepository;
    private final GroupSettingService groupSettingService;
    private final CategoryService categoryService;
    private final GroupService groupService;
    private final GroupEventConverter groupEventConverter;

    @Override
    public GetOneGroupEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month) {
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다.");
        }

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);
        List<OneGroupAllEventResDTO> groupDTOs = groupEventRepository.findByGroup_GroupIdAndStartDateBetween(groupId, startDate, endDate)
                .stream()
                .map(groupEvent -> {
            // groupEvent를 DTO로 변환
            OneGroupAllEventResDTO groupDTO = groupEventConverter.groupEventToOneGroupAllEventResDTO(groupEvent);
                    groupDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(groupDTO.getGroupEventId()));

            return groupDTO;
        })
                .toList();

        return new GetOneGroupEventResDTO(group, groupCategory, groupDTOs);
    }

    @Override
    public GetOneGroupEventResDTO getGroupSomeOneEvents(UUID groupId, UUID someoneUserId, UUID userId, String year, String month) {
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
        List<OneGroupSomeoneEventResDTO> groupEventDTOs = groupEventRepository.findByGroupIdAndUserIdAndStartDateBetween(groupId, someoneUserId, startDate, endDate)
                .stream()
                .map(groupEventConverter::groupEventToOneGroupSomeoneEventResDTO)
                .toList();

        return new GetOneGroupEventResDTO(group, groupCategory, groupEventDTOs);
    }

    @Override
    public GetAllGroupSomeoneEventResDTO getGroupMyEvents(UUID userId, String year, String month) {
        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = groupService.getGroupByUserId(userId);
        List<CategoryResDTO> categoryResDTOs = categoryService.getOnlyGroupCategoryByUserId(userId);
        List<AllGroupSomeoneEventResDTO> groupEventDTOs = groupEventRepository.findByUserIdAndStartDateBetween(userId, startDate, endDate)
                .stream()
                .map(groupEventConverter::groupEventToAllGroupSomeoneEventResDTO)
                .toList();

        return new GetAllGroupSomeoneEventResDTO(groupDTOs, categoryResDTOs, groupEventDTOs);
    }

    @Override
    public OneGroupAllEventResDTO createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId) {
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

        OneGroupAllEventResDTO groupEventDTO = groupEventConverter.groupEventToOneGroupAllEventResDTO(savedGroupEvent);
        groupEventDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(savedGroupEvent.getGroupEventId()));

        return groupEventDTO;
    }

    @Override
    public OneGroupAllEventResDTO updateGroupEvent(UUID groupId, UUID groupEventId, GroupEventUpdateReqDTO groupEventUpdateReqDTO, UUID userId) {
        // 사용자가 해당 그룹에 속하지 않는 에러 처리
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException(userId + "사용자는 해당 그룹에 속하지 않습니다.");
        }

        GroupEvent existedGroupEvent = groupEventRepository.findById(groupEventId)
                .orElseThrow(() -> new GroupEventNotFoundException(groupEventId + "인 그룹 일정을 찾을 수 없습니다."));

        GroupEvent updateGroupEvent = groupEventConverter.groupEventUpdateReqDTOToGroupEvent(groupEventUpdateReqDTO, existedGroupEvent);
        updateGroupEvent.updateGroup(groupService.findGroupEntityById(groupId));

        groupEventRepository.save(updateGroupEvent);

        List<UUID> existedAssignedMemberList = groupSettingService.getGroupEventRelatedUsers(updateGroupEvent.getGroupEventId());

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
                        .groupEvent(updateGroupEvent)
                        .groupSetting(groupSettingService.findGroupSettingByGroupIdAndUserId(groupId, uuid))
                        .build();

                groupEventMappingRepository.save(groupEventMapping);
            });
        }

        OneGroupAllEventResDTO groupEventDTO = groupEventConverter.groupEventToOneGroupAllEventResDTO(updateGroupEvent);
        groupEventDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(updateGroupEvent.getGroupEventId()));

        return groupEventDTO;
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
