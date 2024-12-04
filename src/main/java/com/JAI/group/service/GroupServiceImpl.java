package com.JAI.group.service;

import com.JAI.category.mapper.CategoryConverter;
import com.JAI.category.service.CategoryService;
import com.JAI.category.service.request.CreateGroupCategoryServiceReq;
import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.request.GroupUpdateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.controller.response.GroupUpdateRes;
import com.JAI.group.converter.GroupConverter;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.exception.GroupNotOwnerException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupService {
    //converter
    private final GroupConverter groupConverter;
    private final CategoryConverter categoryConverter;
    private final GroupSettingConverter groupSettingConverter;
    //repository
    private final GroupRepository groupRepository;
    //service
    private final GroupSettingService groupSettingService;
    private final CategoryService categoryService;


    @Override
    @Transactional
    public GroupCreateRes createGroup(GroupCreateReq req, CustomUserDetails user) {

        //받아온 값으로 엔티티 생성
        //그룹 DB에 해당 그룹 저장
        Group groupEntity = groupRepository.save(groupConverter.toGroupCreateEntity(req));

        //현재 유저 그룹세팅 DB에 저장 할 수 있게 dto로 변환
        //이거 디티오는 어디 패키지에 넣어야 될까 -> 그룹셋팅에 넣어야지
        AddGroupMemberServiceReq addGroupMemberServiceReq =
                groupSettingConverter.toAddGroupMemberServiceReq(groupEntity, user.getUser(), GroupRole.LEADER);

        //그룹 세팅에 멤버 저장하는 메서드 호출
        groupSettingService.addGroupMember(addGroupMemberServiceReq);

        //카테고리에 저장할 수 있도록 dto로 변환
        CreateGroupCategoryServiceReq createGroupCategoryServiceReq =
                categoryConverter.toCreateGroupCategoryServiceReq(groupEntity, req.getGroupColor(), user.getUser());

        //카테고리 생성하는 메서드 호출
        UUID categoryId = categoryService.addGroupCategory(createGroupCategoryServiceReq);

        //각각의 생성 후 호출값 모아서 response로 반환
        GroupCreateRes groupCreateRes =
                groupConverter.toGroupCreateDTO(groupEntity, user.getUser(), categoryId);

        return groupCreateRes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListRes> getGroupList(CustomUserDetails user) {
        List<UUID> groupIdList = groupSettingService.getGroupIdList(user.getUser().getUserId());
        List<Group> groupList = groupRepository.findAllById(groupIdList);

        return groupList.stream()
                .map(groupConverter::toGroupListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupUpdateRes updateGroupInfo(UUID groupId, GroupUpdateReq req, CustomUserDetails user) {
        //id로 그룹 찾아와
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));

        //현재 유저가 해당 그룹 인지 체크, leader인지 체크
        checkGroupLeader(groupId, user.getUser().getUserId());

        //그룹 카테고리 이름, 색상 변경 -->
        categoryService.updateGroupCategoryColor(group.getGroupId(), req.getName(), req.getColor());

        //그룹 설명 변경
        group.updateGroupInfo(req.getName(), req.getDescription());
        groupRepository.save(group);

        //바뀐 정보들로 DTO 구성
        return groupConverter.toGroupUpdateDTO(group, req.getColor());
    }

    @Override
    @Transactional
    public void deleteGroup(UUID groupId, CustomUserDetails user) {
        //id로 그룹 찾아와
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));

        //현재 유저가 해당 그룹 인지 체크, leader인지 체크
        checkGroupLeader(groupId, user.getUser().getUserId());

        //그룹 삭제
        groupRepository.delete(group);
    }

    public void checkGroupLeader(UUID groupId, UUID userId){
        GroupRole role = groupSettingService.findGroupMemberRole(groupId, userId);
        if(role.equals(GroupRole.MEMBER)) {
            throw new GroupNotOwnerException("접근 권한이 없습니다.");
        }
    }

    public GroupListRes getGroupById(UUID groupId) {
        return groupConverter.toGroupListDTO(
                groupRepository.findById(groupId)
                        .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다.")));
    }

    @Override
    public List<GroupListRes> getGroupByUserId(UUID userId) {
        return groupRepository.findGroupByUserId(userId)
                .stream()
                .map(groupConverter::toGroupListDTO)
                .toList();
    }
}
