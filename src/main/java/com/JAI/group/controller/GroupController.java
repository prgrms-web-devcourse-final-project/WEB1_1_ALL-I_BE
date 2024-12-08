package com.JAI.group.controller;


import com.JAI.global.controller.ApiResponse;
import com.JAI.group.controller.request.GroupCreateReq;
import com.JAI.group.controller.request.GroupUpdateReq;
import com.JAI.group.controller.response.GroupCreateRes;
import com.JAI.group.controller.response.GroupListRes;
import com.JAI.group.controller.response.GroupUpdateRes;
import com.JAI.group.service.GroupService;
import com.JAI.user.service.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "그룹 & 그룹 카테고리 생성", description = "그룹 생성 요청 API")
    @Parameters({
            @Parameter(name = "groupName", description = "그룹 이름(NOT NULL)", example = "득근득근"),
            @Parameter(name = "description", description = "그룹 설명", example = "모두 같이 운동하고 건강한 삶을 지향해요!"),
            @Parameter(name = "groupColor", description = "그룹 카테고리 색상", example = "#234567")
    })
    public ApiResponse<GroupCreateRes> createGroup(@RequestBody @Valid GroupCreateReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onCreateSuccess(groupService.createGroup(req, user));
    }

    @GetMapping
    @Operation(summary = "사용자의 그룹 조회 요청", description = "사용자의 그룹 조회 요청 API / a 사용자가 b, c 그룹에 속해 있다면 b, c 그룹 정보 반환")
    public ApiResponse<List<GroupListRes>> getGroupList(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupService.getGroupList(user));
    }

    @PatchMapping("/{groupId}")
    @Operation(summary = "그룹 & 그룹 카테고리 수정", description = "그룹 수정 요청 API")
    @Parameters({
            @Parameter(name = "name", description = "그룹 이름(NOT NULL)", example = "득근득근 두근두근"),
            @Parameter(name = "description", description = "그룹 설명", example = "운동하고 건강한 삶을 지향해요!"),
            @Parameter(name = "color", description = "그룹 카테고리 색상", example = "#234568")
    })
    public ApiResponse<GroupUpdateRes> updateGroupInfo(@PathVariable UUID groupId, @RequestBody GroupUpdateReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(groupService.updateGroupInfo(groupId, req, user));
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "그룹 & 그룹 카테고리 삭제", description = "그룹 삭제 요청 API")
    public ApiResponse<String> deleteGroup(@PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user) {
        groupService.deleteGroup(groupId, user);
        return ApiResponse.onDeleteSuccess("그룹이 정상적으로 삭제되었습니다.");
    }
}
