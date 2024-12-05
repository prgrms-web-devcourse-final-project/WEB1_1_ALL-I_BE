package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.request.GroupTodoStateReq;
import com.JAI.todo.controller.request.GroupTodoUpdateReq;
import com.JAI.todo.controller.response.*;
import com.JAI.todo.converter.GroupTodoConverter;
import com.JAI.todo.service.GroupTodoMappingService;
import com.JAI.todo.service.GroupTodoService;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group-todos")
public class GroupTodoController {

    private final GroupTodoService groupTodoService;
    private final GroupTodoMappingService groupTodoMappingService;
    private final GroupTodoConverter groupTodoConverter;

    @PostMapping("/{groupId}/todo")
    public ApiResponse<GroupTodoCreateRes> createGroupTodo(@RequestBody @Valid GroupTodoCreateReq req, @PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onCreateSuccess(groupTodoService.createGroupTodo(req, groupId, user.getUserId()));
    }

    @GetMapping("/{groupId}")
    public ApiResponse<GroupTodoInfoRes> getGroupTodos (@PathVariable("groupId") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getGroupTodos(groupId, user.getUserId(), year, month));
    }

    @GetMapping()
    public ApiResponse<GroupTodoInfoRes> getMyGroupTodos(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getMyGroupTodos(user.getUserId(), year, month));
    }

    @GetMapping("/{groupId}/todos/{userId}")
    public ApiResponse<GroupTodoInfoRes> getGroupMemberGroupTodos(@PathVariable("groupId") UUID groupId, @PathVariable("userId") UUID groupMemberId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getGroupMemberGroupTodos(groupId, groupMemberId, user.getUserId(), year, month));
    }

    @PatchMapping("/{groupId}/todos/{groupTodoId}/state")
    public ApiResponse<GroupTodoStateRes> updateGroupTodoState(@RequestBody GroupTodoStateReq req, @PathVariable UUID groupId, @PathVariable UUID groupTodoId, @AuthenticationPrincipal CustomUserDetails user){
        //그룹 투두 맵핑에서 각 done 값 변경
        GroupMemberStateRes groupMemberStateRes =
                groupTodoMappingService.updateGroupTodoMappingState(req, groupId, groupTodoId, user.getUserId());
        //그룹 투두에서 그룹 투두 아이디로 현재 done 상태 체크
        GroupTodoRes groupTodoRes = groupTodoService.updateGroupTodoState(groupTodoId);

        return ApiResponse.onSuccess(GroupTodoStateRes.builder().groupTodoRes(groupTodoRes).groupMemberStateRes(groupMemberStateRes).build());
    }

    @PatchMapping("/{groupId}/todos/{groupTodoId}/info")
    public ApiResponse<GroupTodoUpdateRes> updateGroupTodoInfo(@RequestBody GroupTodoUpdateReq req, @PathVariable UUID groupId, @PathVariable UUID groupTodoId, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.updateGroupTodoInfo(req, groupTodoId, groupId, user.getUserId()));
    }

    @DeleteMapping("/{groupId}/todos/{groupTodoId}")
    public ApiResponse<String> deleteGroupTodo(@PathVariable UUID groupTodoId,@PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user){
        groupTodoService.deleteGroupTodo(groupTodoId, groupId, user.getUserId());
        return ApiResponse.onDeleteSuccess("그룹 투두가 성공적으로 삭제되었습니다.");
    }
}
