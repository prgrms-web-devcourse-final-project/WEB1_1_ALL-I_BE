package com.JAI.todo.service;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.domain.Group;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.service.GroupService;
import com.JAI.group.service.GroupSettingService;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.*;
import com.JAI.todo.converter.GroupTodoConverter;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GroupTodoServiceImpl implements GroupTodoService{
    private final GroupTodoRepository groupTodoRepository;
    private final GroupRepository groupRepository;
    private final GroupTodoConverter groupTodoConverter;
    private final GroupTodoMappingService groupTodoMappingService;
    private final GroupService groupService;
    private final GroupSettingService groupSettingService;
    private final CategoryService categoryService;

    @Override
    public GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId) {
        // TODO :: 현재 로그인 된 유저를 검증해야할까..
        //그룹 아이디로 그룹
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 Id웅앵ㅇ우"));

        GroupTodo groupTodo = groupTodoConverter.toGroupTodoEntity(req, group);
        //투두 생성
        groupTodoRepository.save(groupTodo);

        //groupTodoMapping 서비스 호출
        List<UUID> groupTodoMappingIds =
            groupTodoMappingService.createGroupTodoMapping(groupTodo.getGroupTodoId(), req.getUserIdList());

        return GroupTodoCreateRes.builder()
                .groupTodoId(groupTodo.getGroupTodoId())
                .groupTodoMappingId(groupTodoMappingIds)
                .build();
    }

    @Override
    public AllGroupTodoRes getGroupTodos(UUID groupId, UUID userId, String year, String month) {

        //그룹 유효한 지
        if(!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupNotFoundException("사용자는 해당 그룹에 속하지 않습니다");
        }

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);

        List<GroupTodoRes> groupTodoResList =
                groupTodoRepository.findByGroup_GroupIdAndDateBetween(groupId, startDate, endDate)
                        .stream()
                        .map(groupTodo ->{
                            GroupTodoRes groupTodoRes = groupTodoConverter.toGroupTodoResDTO(groupTodo);
                            groupTodoRes.updateUserIds(groupSettingService.getGroupTodoRelatedUsers(groupTodoRes.getGroupTodoId()));
                            return groupTodoRes;
                        })
                        .toList();

        return AllGroupTodoRes.builder()
                .group(group)
                .groupCategory(groupCategory)
                .groupTodos(groupTodoResList)
                .build();
    }

    @Override
    public MyGroupTodosRes getMyGroupTodos(UUID userId, String year, String month) {

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = groupService.getGroupByUserId(userId);
        List<CategoryResDTO> categoryResDTOs = categoryService.getOnlyGroupCategoryByUserId(userId);

        List<GroupTodoByUserRes> groupTodos = groupTodoRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(groupTodoConverter::toGroupTodoByUserResDTO)
                .toList();

        return MyGroupTodosRes.builder()
                .groups(groupDTOs)
                .groupCategories(categoryResDTOs)
                .groupTodos(groupTodos)
                .build();
    }

    @Override
    public MemberGroupTodosRes getGroupMemberGroupTodos(UUID groupId, UUID groupMemberId, UUID userId, String year, String month) {

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GroupCategoryResDTO groupCategory = categoryService.getCategoryByGroupId(groupId);
        GroupListRes group = groupService.getGroupById(groupId);

        List<GroupTodoByUserRes> groupTodos = groupTodoRepository.findByGroupIdAndUserIdAndDateBetween(groupId, userId, startDate, endDate)
                .stream()
                .map(groupTodoConverter::toGroupTodoByUserResDTO)
                .toList();

        return MemberGroupTodosRes.builder()
                .group(group)
                .groupCategory(groupCategory)
                .groupTodos(groupTodos)
                .build();
    }

}
