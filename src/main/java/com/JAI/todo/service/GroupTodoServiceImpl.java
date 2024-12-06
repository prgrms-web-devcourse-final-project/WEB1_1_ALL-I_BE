package com.JAI.todo.service;

import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.DTO.GroupCategoryResDTO;
import com.JAI.category.service.CategoryService;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.domain.Group;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.exception.GroupSettingNotFoundException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.service.GroupService;
import com.JAI.group.service.GroupSettingService;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.request.GroupTodoUpdateReq;
import com.JAI.todo.controller.response.*;
import com.JAI.todo.converter.GroupTodoConverter;
import com.JAI.todo.converter.GroupTodoMappingConverter;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.domain.GroupTodoMapping;
import com.JAI.todo.exception.GroupTodoNotFoundException;
import com.JAI.todo.repository.GroupTodoMappingRepository;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Transactional
    public GroupTodoCreateRes createGroupTodo(GroupTodoCreateReq req, UUID groupId, UUID userId) {
        //그룹 멤버인지 검증
        validateGroupMember(groupId, userId);

        //그룹 아이디로 그룹
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));

        GroupTodo groupTodo = groupTodoConverter.toGroupTodoEntity(req, group);
        //투두 생성
        groupTodoRepository.save(groupTodo);

        //groupTodoMapping 서비스 호출
        List<UUID> groupTodoMappingIds =
            groupTodoMappingService.createGroupTodoMapping(groupTodo.getGroupTodoId(), req.getUserIdList());

        return groupTodoConverter.toGroupTodoCreateDTO(groupTodo.getGroupTodoId(), groupTodoMappingIds);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupTodoInfoRes getGroupTodos(UUID groupId, UUID userId, String year, String month) {

        //그룹 멤버인지 검증
        validateGroupMember(groupId, userId);

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = new ArrayList<>();
        groupDTOs.add(groupService.getGroupById(groupId));

        List<CategoryResDTO> categoryResDTOs = new ArrayList<>();
        categoryResDTOs.add(categoryService.getCategoryByGroupId(groupId));


        List<GroupTodoRes> groupTodoResList =
                groupTodoRepository.findByGroup_GroupIdAndDateBetween(groupId, startDate, endDate)
                        .stream()
                        .map(groupTodo ->{
                            GroupTodoRes groupTodoRes = groupTodoConverter.toGroupTodoResDTO(groupTodo);
                            groupTodoRes.updateCategoryId(categoryResDTOs.get(0).getCategoryId());
                            List<GroupMemberStateRes> memberState = groupTodoMappingService.getMemberStateByGroupTodoId(groupTodoRes.getGroupTodoId());
                            groupTodoRes.updateUserIdList(memberState);
                            return groupTodoRes;
                        })
                        .toList();

        return groupTodoConverter.toGroupTodoInfoDTO(groupDTOs, categoryResDTOs, groupTodoResList);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupTodoInfoRes getMyGroupTodos(UUID userId, String year, String month) {


        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = groupService.getGroupByUserId(userId);
        List<CategoryResDTO> categoryResDTOs = categoryService.getOnlyGroupCategoryByUserId(userId);

        // categoryResDTOs를 groupId를 키로 가지는 Map으로 변환
        Map<UUID, UUID> groupIdToCategoryIdMap = categoryResDTOs.stream()
                .collect(Collectors.toMap(CategoryResDTO::getGroupId, CategoryResDTO::getCategoryId));

        List<GroupTodoRes> groupTodos = groupTodoRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(groupTodo ->{
                    GroupTodoRes groupTodoRes = groupTodoConverter.toGroupTodoResDTO(groupTodo);
                    UUID categoryId = groupIdToCategoryIdMap.get(groupTodoRes.getGroupId());
                    groupTodoRes.updateCategoryId(categoryId);
                    //groupTodoRes.updateUserIds(groupSettingService.getGroupTodoRelatedUsers(groupTodoRes.getGroupTodoId()));
                    List<GroupMemberStateRes> memberState = groupTodoMappingService.getMemberStateByGroupTodoId(groupTodoRes.getGroupTodoId());
                    groupTodoRes.updateUserIdList(memberState);
                    return groupTodoRes;
                })
                .toList();

        return groupTodoConverter.toGroupTodoInfoDTO(groupDTOs, categoryResDTOs, groupTodos);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupTodoInfoRes getGroupMemberGroupTodos(UUID groupId, UUID groupMemberId, UUID userId, String year, String month) {

        //그룹 멤버인지 검증
        validateGroupMember(groupId, userId);

        // 조회할 일정의 범위 설정
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<GroupListRes> groupDTOs = new ArrayList<>();
        groupDTOs.add(groupService.getGroupById(groupId));

        List<CategoryResDTO> categoryResDTOs = new ArrayList<>();
        categoryResDTOs.add(categoryService.getCategoryByGroupId(groupId));

        List<GroupTodoRes> groupTodos = groupTodoRepository.findByGroupIdAndUserIdAndDateBetween(groupId, userId, startDate, endDate)
                .stream()
                .map(groupTodo ->{
                    GroupTodoRes groupTodoRes = groupTodoConverter.toGroupTodoResDTO(groupTodo);
                    groupTodoRes.updateCategoryId(categoryResDTOs.get(0).getCategoryId());
                    List<GroupMemberStateRes> memberState = groupTodoMappingService.getMemberStateByGroupTodoId(groupTodoRes.getGroupTodoId());
                    groupTodoRes.updateUserIdList(memberState);
                    return groupTodoRes;
                })
                .toList();

        return groupTodoConverter.toGroupTodoInfoDTO(groupDTOs, categoryResDTOs, groupTodos);
    }

    @Override
    @Transactional
    public GroupTodoRes updateGroupTodoState(UUID groupTodoId) {
        //그룹 투두 아이디로 현재 그룹 투두 조회
        GroupTodo groupTodo = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new GroupTodoNotFoundException("해당 ID의 그룹 투두가 존재하지 않습니다."));

        //모든 할당자가 완료했는지 체크
        groupTodo.updateGroupTodoState(groupTodoMappingService.checkGroupTodoState(groupTodoId));

        //현재 사항 DB에 저장
        groupTodoRepository.save(groupTodo);

        return groupTodoConverter.toGroupTodoResDTO(groupTodo);
    }

    @Override
    @Transactional
    public GroupTodoUpdateRes updateGroupTodoInfo(GroupTodoUpdateReq req, UUID groupTodoId, UUID groupId, UUID userId) {
        //그룹 멤버인지 검증
        validateGroupMember(groupId, userId);

        //기존 값 가져오기
        GroupTodo groupTodoEntity = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new GroupTodoNotFoundException("해당 ID의 그룹 투두를 찾을 수 없습니다."));

        groupTodoEntity.updateGroupTodoInfo(req.getTitle(), req.getDate(), req.getStartTime());

        //변경된 값 저장
        groupTodoRepository.save(groupTodoEntity);

        //할당자 변경
        List<UUID> userIdList = groupTodoMappingService.updateGroupTodoMappingUser(req.getUserIdList(), groupTodoId, groupId);

        return groupTodoConverter.toGroupTodoUpdateDTO(groupTodoEntity, userIdList);
    }

    @Override
    @Transactional
    public void deleteGroupTodo(UUID groupTodoId, UUID groupId, UUID userId) {

        //그룹 멤버인지 검증
        validateGroupMember(groupId, userId);

        GroupTodo deleteTodo = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new GroupTodoNotFoundException("해당 ID의 그룹 투두를 찾을 수 없습니다."));

        groupTodoRepository.delete(deleteTodo);
    }

    public void validateGroupMember(UUID groupId, UUID userId) {
        if(!groupSettingService.isGroupMemberExisted(groupId, userId)) {
            throw new GroupSettingNotFoundException("해당 그룹의 멤버가 아닙니다.");
        }
    }

}
