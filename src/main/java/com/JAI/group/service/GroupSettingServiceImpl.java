package com.JAI.group.service;

import com.JAI.group.controller.response.GroupMemberListRes;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.domain.GroupSetting;
import com.JAI.group.exception.*;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.repository.GroupSettingRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.group.service.response.GroupSettingRes;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import com.JAI.user.service.dto.CustomUserDetails;
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

    // 그룹원 조회
    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberListRes> getGroupMembers(UUID groupId) {
        List<GroupSetting> groupSettings = groupSettingRepository.findByGroup_GroupIdWithUser(groupId);

        return groupSettings.stream()
                .map(gs -> groupSettingConverter.toGroupMemberListDTO(gs, gs.getUser().getNickname()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void quitGroupMember(UUID groupSettingId, CustomUserDetails user) {
        //해당 멤버 정보 찾기
        GroupSetting deleteMember = findGroupMemberById(groupSettingId);

        //본인인지 확인
        if(!deleteMember.getUser().getUserId().equals(user.getUser().getUserId())){
            throw new GroupSettingNotOwnerException("유저 본인만 탈퇴 가능합니다.");
        }

        groupSettingRepository.delete(deleteMember);
    }

    @Override
    @Transactional
    public void ejectionGroupMember(UUID groupSettingId, CustomUserDetails user) {
        //삭제할 멤버 정보 찾기
        GroupSetting deleteMember = findGroupMemberById(groupSettingId);

        UUID groupId = deleteMember.getGroup().getGroupId();
        //리더 정보
        GroupSetting leader = findGroupSettingByGroupIdAndUserId(groupId, user.getUser().getUserId());

        //리더만 가능
        if(leader.getRole() != GroupRole.LEADER){
            throw new GroupSettingAccessDeniedException("그룹 리더만 접근 가능합니다.");
        }

        groupSettingRepository.delete(deleteMember);

    }

    public GroupSetting findGroupMemberById(UUID groupSettingId){
        return groupSettingRepository.findById(groupSettingId)
                .orElseThrow(() -> new GroupSettingNotFoundException("해당 그룹에 존재하지 않는 멤버입니다."));
    }

    @Override
    public GroupSetting findGroupSettingByGroupIdAndUserId(UUID groupId, UUID userId){
        return groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .orElseThrow(() -> new GroupSettingNotFoundException("해당 그룹에 존재하지 않는 멤버입니다."));
    }

    @Override
    public UUID findIdByGroupIdAndUserId(UUID groupId, UUID userId){
        GroupSetting groupSetting =  groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                                        .orElseThrow(() -> new GroupSettingNotFoundException("해당 그룹에 존재하지 않는 멤버입니다."));
        return groupSetting.getGroupSettingId();
    }

    @Override
    public GroupRole findGroupMemberRole(UUID groupId, UUID userId){
        GroupSetting groupSetting = groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .orElseThrow(() -> new GroupSettingNotOwnerException("해당 그룹 멤버가 아닙니다."));
        return groupSetting.getRole();
    }

    public GroupSettingRes findGroupIdAndRole(UUID groupId, UUID userId){
        GroupSetting groupSetting = groupSettingRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId)
                .orElseThrow(() -> new GroupSettingNotOwnerException("해당 그룹 멤버가 아닙니다."));
        return GroupSettingRes.builder()
                .groupSettingId(groupSetting.getGroupSettingId())
                .role(groupSetting.getRole())
                .build();
    }


    @Override
    public void findGroupMemberExisted(UUID groupId, UUID userId) {
        if(groupSettingRepository.existsByGroup_GroupIdAndUser_UserId(groupId, userId)){
            throw new GroupSettingDuplicatedException("이미 존재하는 멤버입니다.");
        }
    }

    @Override
    public boolean isGroupMemberExisted(UUID groupId, UUID userId) {
        return groupSettingRepository.existsByGroup_GroupIdAndUser_UserId(groupId, userId);
    }

    @Override
    public List<UUID> getGroupTodoRelatedUsers(UUID groupTodoId){
        return groupSettingRepository.findByGroupTodoGroupId(groupTodoId);
    }

    @Override
    public List<UUID> getGroupEventRelatedUsers(UUID groupEventId) {
        return groupSettingRepository.findUserIdByGroupEventGroupId(groupEventId);
    }
}
