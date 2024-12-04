package com.JAI.group.service;

import com.JAI.group.domain.GroupRole;
import com.JAI.group.domain.GroupSetting;
import com.JAI.group.service.response.GroupSettingRes;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GroupCheckService {
    private final GroupService groupService;
    private final GroupSettingService groupSettingService;

    @Transactional
    public String checkRoleAndHandleQuit(UUID groupId, CustomUserDetails user){
        GroupSettingRes dto = groupSettingService.findGroupIdAndRole(groupId, user.getUserId());

        if(dto.getRole() == GroupRole.LEADER){
            //그룹 삭제 메소드
            groupService.deleteGroup(groupId, user);
            return "그룹이 정상적으로 삭제되었습니다.";
        }
        else if(dto.getRole() == GroupRole.MEMBER){
            //그룹 멤버 탈퇴 메소드
            groupSettingService.quitGroupMember(dto.getGroupSettingId(), user);
            return "그룹 탈퇴에 성공하였습니다.";
        }
        return "유효하지 않은 역할입니다.";
    }

}
