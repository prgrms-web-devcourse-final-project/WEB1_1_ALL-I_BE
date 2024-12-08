package com.JAI.group.service;

import com.JAI.alarm.service.AlarmService;
import com.JAI.group.controller.request.GroupMemberInviteReq;
import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.converter.GroupInvitationConverter;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.*;
import com.JAI.group.exception.*;
import com.JAI.group.repository.GroupInvitationRepository;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.converter.UserConverter;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import com.JAI.user.service.dto.CustomUserDetails;
import com.JAI.user.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// TODO :: 그룹 멤버
@RequiredArgsConstructor
@Service
public class GroupInvitationServiceImpl implements GroupInvitationService{
    //Repository
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupRepository groupRepository;
    //Service
    private final GroupSettingService groupSettingService;
    private final AlarmService alarmService;
    private final UserRepository userRepository;
    private final GroupInvitationConverter groupInvitationConverter;
    private final GroupSettingConverter groupSettingConverter;
    private final UserConverter userConverter;

    // 그룹 멤버 초대
    @Override
    @Transactional
    public GroupMemberInviteRes inviteGroupMember(UUID groupId, GroupMemberInviteReq req, CustomUserDetails user) {

        //유효한 그룹아이디인지 검증->검증 후 바로 저장할 것이기 때문에 레포지토리로 호출
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 ID의 그룹을 찾을 수 없습니다."));

        //현재 유저가 팀장인지 체크
        checkGroupLeader(groupId, user.getUser().getUserId());

        //리퀘스트로 들어온 닉네임이 유효한지 검증
        User userEntity = userRepository.findByNickname(req.getNickname())
                .orElseThrow(() -> new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다."));

        //이미 요청된 초대가 있는지 검증
        alreadyInvited(groupId, userEntity.getUserId());
        //이미 그룹 멤버인지 검증
        groupSettingService.findGroupMemberExisted(groupId, userEntity.getUserId());

        //Pending상태로 DB에 저장
        GroupInvitation groupInvitation = groupInvitationConverter.toGroupInvitationEntity(group, userEntity);
        groupInvitationRepository.save(groupInvitation);

        //알림 메서드 호출
        UserDTO sender = UserDTO.builder()
                .userId(user.getUser().getUserId())
                .nickname(user.getUser().getNickname())
                .email(user.getUser().getEmail())
                .build();

        alarmService.createGroupInvitationAlarm(groupInvitationConverter.toGroupInvitationDTO(groupInvitation, sender));

        //등록내용 반환
        return groupInvitationConverter.toGroupMemberInviteDTO(groupInvitation, req.getNickname());
    }

    // 그룹원 초대 수락
    @Override
    @Transactional
    public void acceptInvitation(UUID groupInvitationId, CustomUserDetails user) {
        //아이디로 그룹초대 정보
        GroupInvitation groupInvitation = checkGroupInvitation(groupInvitationId);
        //그룹 초대 유저와 현재 로그인 유저가 동일한지
        checkUserAuthority(groupInvitation.getUser().getUserId(), user.getUser().getUserId());

        //상태를 수락으로 변경
        groupInvitation.updateStatus(InvitationStatus.ACCEPTED);
        groupInvitationRepository.save(groupInvitation);

        //그룹 멤버로 DB에 저장
        AddGroupMemberServiceReq req = groupSettingConverter.toAddGroupMemberServiceReq(
                groupInvitation.getGroup(), user.getUser(), GroupRole.MEMBER);
        groupSettingService.addGroupMember(req);

        // 그룹 초대 알림 삭제
        alarmService.deleteAlarmByGroupInvitationId(groupInvitationId);
    }


    // 그룹원 초대 거절
    @Override
    @Transactional
    public void declineInvitation(UUID groupInvitationId, CustomUserDetails user) {
        //아이디로 그룹초대 정보
        GroupInvitation groupInvitation = checkGroupInvitation(groupInvitationId);
        //그룹 초대 유저와 현재 로그인 유저가 동일한지
        checkUserAuthority(groupInvitation.getUser().getUserId(), user.getUser().getUserId());

        //상태를 거절로 변경
        groupInvitation.updateStatus(InvitationStatus.DECLINED);
        groupInvitationRepository.save(groupInvitation);

        // 그룹 초대 알림 삭제
        alarmService.deleteAlarmByGroupInvitationId(groupInvitationId);
    }


    public void checkGroupLeader(UUID groupId, UUID userId){
        GroupRole role = groupSettingService.findGroupMemberRole(groupId, userId);
        if(role.equals(GroupRole.MEMBER)) {
            throw new GroupNotOwnerException("접근 권한이 없습니다.");
        }
    }


    public void checkUserAuthority(UUID groupInvitationUserId, UUID currentLoginUserId){
        if(!groupInvitationUserId.equals(currentLoginUserId)) {
            throw new GroupInvitationAccessDeniedException("접근 권한이 없습니다.");
        }
    }


    public void alreadyInvited(UUID groupId, UUID userId) {
        if(groupInvitationRepository.existsByUser_UserIdAndGroup_GroupIdAndStatus(userId, groupId, InvitationStatus.PENDING)){
            throw new GroupInvitationDuplicatedException("이미 요청된 초대입니다.");
        }
    }

    private GroupInvitation checkGroupInvitation(UUID groupInvitationId) {
        return groupInvitationRepository.findById(groupInvitationId)
                .orElseThrow(() -> new GroupInvitationNotFoundException("해당 ID의 그룹 초대를 찾을 수 없습니다."));
    }
}
