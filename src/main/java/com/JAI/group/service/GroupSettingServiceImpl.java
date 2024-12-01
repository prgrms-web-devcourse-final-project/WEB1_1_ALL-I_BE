package com.JAI.group.service;

import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.converter.GroupSettingConverter;
import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupSetting;
import com.JAI.group.repository.GroupRepository;
import com.JAI.group.repository.GroupSettingRepository;
import com.JAI.group.service.request.AddGroupMemberServiceReq;
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
public class GroupSettingServiceImpl implements GroupSettigService{

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
                .orElseThrow(() -> new RuntimeException("Group not found"));
        //dto에서 받은 유저아이디로 유저 찾기
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //DB에 저장
        groupSettingRepository.save(
                groupSettingConverter.toGroupSettingEntity(req.getRole(), user, group)
        );
    }

    @Override
    public List<UUID> getGroupIdList(User user) {
        //해당 유저가 포함되어있는 그룹 아이디 리스트 반환
        return groupSettingRepository.findByUser(user).stream()
                .map(groupSetting -> groupSetting.getGroup().getGroupId())
                .distinct()
                .collect(Collectors.toList());
    }
}
