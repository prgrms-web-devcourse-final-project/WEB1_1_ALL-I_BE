package com.JAI.group.service;

import com.JAI.group.controller.request.GroupMemberInviteReq;
import com.JAI.group.controller.response.GroupMemberInviteRes;
import com.JAI.group.converter.GroupInvitationConverter;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupInvitation;
import com.JAI.group.domain.GroupRole;
import com.JAI.group.domain.Status;
import com.JAI.group.exception.GroupNotFoundException;
import com.JAI.group.exception.GroupNotOwnerException;
import com.JAI.group.repository.GroupInvitationRepository;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import com.JAI.user.service.dto.CustomUserDetails;
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
    private final UserRepository userRepository;
    private final GroupInvitationConverter groupInvitationConverter;
    private final GroupSettingConverter groupSettingConverter;

    // TODO :: 그룹 멤버 초대
    @Override
    @Transactional
    public GroupMemberInviteRes inviteGroupMember(UUID groupId, GroupMemberInviteReq req, CustomUserDetails user) {

        //유효한 그룹아이디인지 검증->검증 후 바로 저장할 것이기 때문에 레포지토리로 호출
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 Id의 그룹이 읎어"));

        //현재 유저가 팀장인지 체크
        checkGroupLeader(groupId, user.getUser().getUserId());

        //리퀘스트로 들어온 닉네임이 유효한지 검증
        User findUser = userRepository.findByNickname(req.getNickname())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        //이미 멤버로 초대되어 있는지 검증
        //group-setting에 있는지
        alreadyInvited(groupId, findUser.getUserId());
        //group-invitation에 있는지
        groupSettingService.findGroupMemberExisted(groupId, findUser.getUserId());

        //그룹 인바이트 디비에 펜딩으로 저장
        GroupInvitation groupInvitation = groupInvitationConverter.toGroupInvitationEntity(group, findUser);
        groupInvitationRepository.save(groupInvitation);
        //알림 메서드 호출 부

        //등록내용 반환
        return groupInvitationConverter.toGroupMemberInviteDTO(groupInvitation, req.getNickname());
    }

    // TODO :: 그룹원 초대 수락
    @Override
    public void acceptInvitation(UUID groupInvitationId, CustomUserDetails user) {
        //패스로 넘어온 그룹 초대 아이디가 유효한지 -> 엔티티로 받아
        GroupInvitation groupInvitation = groupInvitationRepository.findById(groupInvitationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));
        //위의 엔티티의 유저와 현재 로그인 유저가 동일한지
        if(!user.getUser().getUserId().equals(groupInvitation.getUser().getUserId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        //상태를 수락으로 변경
        groupInvitation.updateStatus(Status.ACCEPT);
        groupInvitationRepository.save(groupInvitation);

        //그룹 셋팅 DB에 저장해야지 -->그룹생성시 썻던 거 불러오면 될듯
        AddGroupMemberServiceReq req = groupSettingConverter.toAddGroupMemberServiceReq(
                groupInvitation.getGroup(), user.getUser(), GroupRole.MEMBER);
        groupSettingService.addGroupMember(req);
    }

    // TODO :: 그룹원 초대 거절
    @Override
    public void declineInvitation(UUID groupInvitationId, CustomUserDetails user) {
        //패스로 넘어온 그룹 초대 아이디가 유효한지 -> 엔티티로 받아
        GroupInvitation groupInvitation = groupInvitationRepository.findById(groupInvitationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));
        //위의 엔티티의 유저와 현재 로그인 유저가 동일한지
        if(!user.getUser().getUserId().equals(groupInvitation.getUser().getUserId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        //상태를 수락으로 변경
        groupInvitation.updateStatus(Status.DECLINED);
        groupInvitationRepository.save(groupInvitation);
    }

    // TODO :: 그룹 멤버 초대
    public void checkGroupLeader(UUID groupId, UUID userId){
        GroupRole role = groupSettingService.findGroupMemberRole(groupId, userId);
        if(role.equals(GroupRole.MEMBER)) {
            throw new GroupNotOwnerException("접근 권한이 없습니다.");
        }
    }

    public void alreadyInvited(UUID groupId, UUID userId) {
        if(groupInvitationRepository.existsByUser_UserIdAndGroup_GroupIdAndStatus(userId, groupId, Status.PENDING)){
            throw new RuntimeException("이미 요청된 초대입니다.");
        }
    }
}
