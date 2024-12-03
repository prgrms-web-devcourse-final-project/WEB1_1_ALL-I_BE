package com.JAI.group.service;

import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.domain.GroupSetting;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.exception.GroupSettingNotOwnerException;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.repository.GroupSettingRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupSettingServiceImpl implements GroupSettingService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupSettingRepository groupSettingRepository;
    private final GroupSettingConverter groupSettingConverter;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGroupMember(AddGroupMemberServiceReq req) {
        //dto에서 받은 그룹아이디로 그룹 찾기
        // TODO :: 여기 예외처리
        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));
        //dto에서 받은 유저아이디로 유저 찾기
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        //DB에 저장
        groupSettingRepository.save(
                groupSettingConverter.toGroupSettingEntity(req.getRole(), user, group)
        );
    }



    @Override
    @Transactional(readOnly = true)
    public List<UUID> getGroupIdList(UUID userId) {
        //해당 유저가 포함되어있는 그룹 아이디 리스트 반환
        return groupSettingRepository.findByUser_UserId(userId).stream()
                .map(groupSetting -> groupSetting.getGroup().getGroupId())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public GroupRole findGroupMemberRole(UUID groupId, UUID userId){
        GroupSetting groupSetting = groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .orElseThrow(() -> new GroupSettingNotOwnerException("해당 그룹 멤버가 아닙니다."));
        return groupSetting.getRole();
    }

    @Override
    public void findGroupMemberExisted(UUID groupId, UUID userId) {
        if(groupSettingRepository.existsByGroup_GroupIdAndUser_UserId(groupId, userId)){
            throw new RuntimeException("이미 존재하는 멤버입니다.");
        }
    }

    // TODO :: 그룹원 조회
    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberListRes> getGroupMembers(UUID groupId) {
        List<GroupSetting> groupSettings = groupSettingRepository.findByGroup_GroupIdWithUser(groupId);

        return groupSettings.stream()
                .map(gs -> groupSettingConverter.toGroupMemberListDTO(gs, gs.getUser().getNickname()))
                .collect(Collectors.toList());
    }
}
