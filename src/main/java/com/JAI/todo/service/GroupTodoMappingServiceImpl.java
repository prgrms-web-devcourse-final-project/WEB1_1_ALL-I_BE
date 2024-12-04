package com.JAI.todo.service;

import com.JAI.group.domain.GroupSetting;
import com.JAI.group.exception.GroupSettingNotFoundException;
import com.JAI.group.repository.GroupSettingRepository;
import com.JAI.todo.converter.GroupTodoMappingConverter;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.domain.GroupTodoMapping;
import com.JAI.todo.repository.GroupTodoMappingRepository;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
