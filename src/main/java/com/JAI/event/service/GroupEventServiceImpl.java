package com.JAI.event.service;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.event.DTO.request.GroupEventCreateReqDTO;
import com.JAI.event.DTO.response.GetOneGroupAllEventResDTO;
import com.JAI.event.DTO.response.GroupEventResDTO;
import com.JAI.event.mapper.GroupEventConverter;
import com.JAI.event.repository.GroupEventRepository;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.service.GroupService;
import com.JAI.group.service.GroupSettingService;
import com.JAI.group.service.response.GroupSettingRes;
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
    private final GroupSettingService groupSettingService;
    private final CategoryService categoryService;
    private final GroupService groupService;
    private final GroupEventConverter groupEventConverter;

    @Override
    public GetOneGroupAllEventResDTO getGroupEvents(UUID groupId, UUID userId, String year, String month) {
        if (!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다.");
        }

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);
        List<GroupEventResDTO> groupEventResDTOs = groupEventRepository.findByGroup_GroupIdAndStartDateBetween(groupId, startDate, endDate)
                .stream()
                .map(groupEvent -> {
            // groupEvent를 DTO로 변환
            GroupEventResDTO groupEventResDTO = groupEventConverter.groupEventToGroupEventResDTO(groupEvent);
                    groupEventResDTO.updateUserIds(groupSettingService.getGroupEventRelatedUsers(groupEventResDTO.getGroupEventId()));

            return groupEventResDTO;
        })
                .toList();

        return new GetOneGroupAllEventResDTO(group, groupCategory, groupEventResDTOs);
    }

    @Override
    public GetOneGroupAllEventResDTO getGroupMyEvents(UUID userId, String year, String month) {
        return null;
    }

    @Override
    public GetOneGroupAllEventResDTO getGroupEventsByGroupEventId(UUID userId, UUID groupEventId) {
        return null;
    }

    @Override
    public void createGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID userId) {

    }

    @Override
    public GetOneGroupAllEventResDTO updateGroupEvent(GroupEventCreateReqDTO groupEventCreateReqDTO, UUID groupId, UUID groupEventId, UUID userId) {
        return null;
    }

    @Override
    public void deleteGroupEvent(UUID groupId, UUID groupEventId, UUID userId) {

    }
}
