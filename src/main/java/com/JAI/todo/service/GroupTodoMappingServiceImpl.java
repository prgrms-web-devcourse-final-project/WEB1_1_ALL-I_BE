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
import com.JAI.todo.repository.GroupTodoMappingRepository;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    public List<UUID> createGroupTodoMapping(UUID groupTodoId, List<UUID> userIdList) {
        GroupTodo groupTodo = groupTodoRepository.findById(groupTodoId)
                .orElseThrow(() -> new RuntimeException("이걸 왜 다시 찾아야하느지.."));

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
    public void updateGroupTodoMappingState(GroupTodoStateReq req, UUID groupId, UUID groupTodoId, UUID userId) {
        // 현재 유저가 해당 그룹 멤버인지
        //이거 두개로 그룹 셋팅 아이디 받아와 --> 해당 그룹에 속해있다 검증 완
        UUID groupSettingId =  groupSettingService.findIdByGroupIdAndUserId(groupId, userId);

        // 그룹 셋팅 아이디랑 그룹 투두 아이디만 있으면 찾을 수 있음 -> 해당 투두에 할당 되어있다 검증 완
        GroupTodoMapping groupTodoMapping =
                groupTodoMappingRepository.findByGroupSetting_GroupSettingIdAndGroupTodo_GroupTodoId(groupSettingId, groupTodoId)
                    .orElseThrow(()-> new RuntimeException("할당되지 않은 사용자 입니다."));

        //상태 변경
        groupTodoMapping.updateGroupTodoMappingState(req.isState());

        groupTodoMappingRepository.save(groupTodoMapping);
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
