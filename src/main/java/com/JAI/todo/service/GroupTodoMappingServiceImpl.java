package com.JAI.todo.service;

import com.JAI.group.domain.GroupSetting;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.exception.GroupSettingNotFoundException;
import com.JAI.group.repository.GroupSettingRepository;
import com.JAI.group.service.GroupSettingService;
import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.response.GroupMemberStateRes;
import com.JAI.todo.converter.GroupTodoMappingConverter;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.domain.GroupTodoMapping;
import com.JAI.todo.exception.GroupTodoMappingNotOwnerException;
import com.JAI.todo.exception.GroupTodoNotFoundException;
import com.JAI.todo.repository.GroupTodoMappingRepository;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupTodoMappingServiceImpl implements GroupTodoMappingService{
    public final GroupTodoRepository groupTodoRepository;
    public final GroupTodoMappingRepository groupTodoMappingRepository;
    private final GroupSettingRepository groupSettingRepository;

    private final GroupTodoMappingConverter groupTodoMappingConverter;
    private final GroupSettingService groupSettingService;

    @Override
    @Transactional
    public List<UUID> createGroupTodoMapping(UUID groupTodoId, List<UUID> userIdList) {
        GroupTodo groupTodo = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new GroupTodoNotFoundException("해당 ID의 그룹 투두가 존재하지 않습니다."));

        UUID groupId = groupTodo.getGroup().getGroupId();

        List<GroupTodoMapping> mappingList = new ArrayList<>();

        for(UUID userId : userIdList) {
            GroupSetting groupSetting = groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                    .orElse(null);

            if(groupSetting != null) {
                GroupTodoMapping groupTodoMapping =
                        groupTodoMappingConverter.toGroupTodoMappingEntity(groupTodo, groupSetting);
                mappingList.add(groupTodoMapping);
            }
        }

        List<GroupTodoMapping> groupTodoMappingList = groupTodoMappingRepository.saveAll(mappingList);

        return groupTodoMappingList.stream()
                .map(GroupTodoMapping::getGroupTodoMappingId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupMemberStateRes updateGroupTodoMappingState(GroupTodoStateReq req, UUID groupId, UUID groupTodoId, UUID userId) {
        // 현재 유저가 해당 그룹 멤버인지
        //이거 두개로 그룹 셋팅 아이디 받아와 --> 해당 그룹에 속해있다 검증 완
        UUID groupSettingId =  groupSettingService.findIdByGroupIdAndUserId(groupId, userId);

        // 그룹 셋팅 아이디랑 그룹 투두 아이디만 있으면 찾을 수 있음 -> 해당 투두에 할당 되어있다 검증 완
        GroupTodoMapping groupTodoMapping =
                groupTodoMappingRepository.findByGroupSetting_GroupSettingIdAndGroupTodo_GroupTodoId(groupSettingId, groupTodoId)
                    .orElseThrow(()-> new GroupTodoMappingNotOwnerException("그룹 투두가 할당되지 않은 사용자 입니다."));

        //상태 변경
        groupTodoMapping.updateGroupTodoMappingState(req.isState());

        groupTodoMappingRepository.save(groupTodoMapping);

        return groupTodoMappingConverter.toGroupMemberStateDTO(
                groupTodoMapping.getGroupSetting().getUser().getUserId()
                , groupTodoMapping.isDone()
        );
    }

    @Override
    @Transactional
    public List<UUID> updateGroupTodoMappingUser(List<GroupMemberStateRes> userIdList, UUID groupTodoId, UUID groupId) {
        GroupTodo groupTodo = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new GroupTodoNotFoundException("해당 ID의 그룹 투두를 찾을 수 없습니다."));

        //해당 투두 아이디에 맵핑된 값들 찾아오기
        List<GroupTodoMapping> mappingList = groupTodoMappingRepository.findByGroupTodoId(groupTodoId);
        Set<UUID> currentSettingIds = mappingList.stream()
                .map(mapping -> mapping.getGroupSetting().getGroupSettingId())
                .collect(Collectors.toSet());

        //req에서 userId로 groupSetting 찾아옴
        Set<UUID> newSettingIds = userIdList.stream()
                .map(user -> groupSettingService.findIdByGroupIdAndUserId(groupId, user.getUserId()))
                .collect(Collectors.toSet());

        //추가 할 ID들
        Set<UUID> toAdd = new HashSet<>(newSettingIds);
        toAdd.removeAll(currentSettingIds);

        //제거할 ID들
        Set<UUID> toRemove = new HashSet<>(currentSettingIds);
        toRemove.removeAll(newSettingIds);


        //레포지토리에 저장
        toAdd.forEach(groupSettingId -> {
            GroupSetting groupSetting = groupSettingRepository.findById(groupSettingId)
                    .orElseThrow(() -> new GroupSettingNotFoundException("존재하지 않는 ID 입니다."));
            groupTodoMappingRepository.save(groupTodoMappingConverter.toGroupTodoMappingEntity(groupTodo, groupSetting));
        });

        toRemove.forEach(groupSettingId -> {
            groupTodoMappingRepository.deleteByGroupTodoIdAndGroupSettingId(groupTodoId, groupSettingId);
        });

        List<UUID> updatedUserIds = groupTodoMappingRepository.findByGroupTodoId(groupTodoId).stream()
                .map(mapping -> mapping.getGroupSetting().getUser().getUserId())
                .collect(Collectors.toList());
        return updatedUserIds;
    }


    @Transactional(readOnly = true)
    public List<GroupMemberStateRes> getMemberStateByGroupTodoId(UUID groupTodoId) {
        List<GroupTodoMapping> groupTodoMappings = groupTodoMappingRepository.findByGroupTodoId(groupTodoId);

        // 엔티티 데이터를 DTO로 변환
        return groupTodoMappings.stream()
                .map(mapping -> GroupMemberStateRes.builder()
                        .userId(mapping.getGroupSetting().getUser().getUserId())
                        .done(mapping.isDone())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public boolean checkGroupTodoState(UUID groupTodoId) {
        //할당자 수 카운트
        int totalTodoCnt = groupTodoMappingRepository.countByGroupTodoId(groupTodoId);
        //할당자의 done 갯수 카운트
        int doneTodoCnt = groupTodoMappingRepository.countDoneByGroupTodoId(groupTodoId);

        //만약 총 갯수랑 true 갯수가 같으면 groupTodo done true로 변경
        if(totalTodoCnt == doneTodoCnt){
            return true;
        }
        return false;
    }
}
