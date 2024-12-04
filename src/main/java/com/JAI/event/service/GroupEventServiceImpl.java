package com.JAI.event.service;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.*;
import com.JAI.event.domain.GroupEvent;
import com.JAI.event.domain.GroupEventMapping;
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
        System.out.println("groupId : " + groupId + " someoneUserId : " + someoneUserId + " userId : " + userId);
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
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다.");
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
    public GetOneGroupEventResDTO updateGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID groupEventId, UUID userId) {
        return null;
    }

    @Override
    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId) {

    }
}
