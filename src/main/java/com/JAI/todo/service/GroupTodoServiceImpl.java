package com.JAI.todo.service;

import com.JAI.group.domain.Group;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.converter.GroupTodoConverter;
import com.JAI.todo.domain.GroupTodo;
import com.JAI.todo.repository.GroupTodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GroupTodoServiceImpl implements GroupTodoService{
    private final GroupTodoRepository groupTodoRepository;
    private final GroupRepository groupRepository;
    private final GroupTodoConverter groupTodoConverter;
    private final GroupTodoMappingService groupTodoMappingService;

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

}
