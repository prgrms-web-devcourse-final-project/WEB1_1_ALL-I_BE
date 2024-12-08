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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    @PostMapping("/{groupId}/todo")
    @Operation(summary = "그룹 TO DO 생성", description = "그룹 TO DO 생성 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "그룹 TO DO(NOT NULL)", example = "문서 작성"),
            @Parameter(name = "date", description = "그룹 TO DO 시작 날짜(NOT NULL)", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 TO DO 시작 시간", example = "20:00:00"),
            @Parameter(name = "userIdList", description = "TO DO를 할당할 그룹 멤버 아이디 리스트", example =
                    """
                    ["32eda718-fc6c-47ed-82ce-697c5f3f6b28",
                    "8af30641-215d-42a6-aed6-006246e53d6e"]
                    """)
    })
    public ApiResponse<GroupTodoCreateRes> createGroupTodo(@RequestBody @Valid GroupTodoCreateReq req, @PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onCreateSuccess(groupTodoService.createGroupTodo(req, groupId, user.getUserId()));
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "특정 그룹 내 TO DO 한 달치 조회", description = "그룹 내 TO DO 한 달치 조회 요청 API / a 그룹 내 사용자 a, b의 TO DO 조회")
    public ApiResponse<GroupTodoInfoRes> getGroupTodos (@PathVariable("groupId") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getGroupTodos(groupId, user.getUserId(), year, month));
    }

    @GetMapping()
    @Operation(summary = "사용자가 속한 그룹 내의 한 달치 TO DO 조회", description = "그룹 내 TO DO 한 달치 조회 요청 API / a 사용자가 속한 그룹 b, c의 TO DO 조회")
    public ApiResponse<GroupTodoInfoRes> getMyGroupTodos(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getMyGroupTodos(user.getUserId(), year, month));
    }

    @GetMapping("/{groupId}/todos/{userId}")
    @Operation(summary = "사용자가 속한 그룹 내 특정 사용자에게 할당된 한 달치 TO DO 조회", description = "사용자가 속한 그룹 내 특정 사용자에게 할당된 한 달치 TO DO 조회 요청 API / a 사용자가 속한 그룹 b에서 사용자 c의 TO DO 조회")
    public ApiResponse<GroupTodoInfoRes> getGroupMemberGroupTodos(@PathVariable("groupId") UUID groupId, @PathVariable("userId") UUID groupMemberId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getGroupMemberGroupTodos(groupId, groupMemberId, user.getUserId(), year, month));
    }

    @PatchMapping("/{groupId}/todos/{groupTodoId}/state")
    @Operation(summary = "그룹 TO DO 달성으로 상태 변경 요청", description = "그룹 TO DO 달성으로 상태 변경 요청 API")
    @Parameter(name = "state", description = "그룹 TO DO 달성 여부", example = "true")
    public ApiResponse<GroupTodoStateRes> updateGroupTodoState(@RequestBody GroupTodoStateReq req, @PathVariable UUID groupId, @PathVariable UUID groupTodoId, @AuthenticationPrincipal CustomUserDetails user){
        //그룹 투두 맵핑에서 각 done 값 변경
        GroupMemberStateRes groupMemberStateRes =
                groupTodoMappingService.updateGroupTodoMappingState(req, groupId, groupTodoId, user.getUserId());
        //그룹 투두에서 그룹 투두 아이디로 현재 done 상태 체크
        GroupTodoRes groupTodoRes = groupTodoService.updateGroupTodoState(groupTodoId);

        return ApiResponse.onSuccess(GroupTodoStateRes.builder().groupTodoRes(groupTodoRes).groupMemberStateRes(groupMemberStateRes).build());
    }

    @PatchMapping("/{groupId}/todos/{groupTodoId}/info")
    @Operation(summary = "그룹 TO DO 수정", description = "그룹 TO DO 수정 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "그룹 TO DO", example = "개발 문서 작성"),
            @Parameter(name = "date", description = "그룹 TO DO 시작 날짜", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 TO DO 시작 시간", example = "21:00:00"),
            @Parameter(name = "userIdList", description = "TO DO를 할당할 그룹 멤버 아이디 리스트", example =
                    """
                    ["32eda718-fc6c-47ed-82ce-697c5f3f6b28"]
                    """)
    })
    public ApiResponse<GroupTodoUpdateRes> updateGroupTodoInfo(@RequestBody @Valid GroupTodoUpdateReq req, @PathVariable UUID groupId, @PathVariable UUID groupTodoId, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.updateGroupTodoInfo(req, groupTodoId, groupId, user.getUserId()));
    }

    @DeleteMapping("/{groupId}/todos/{groupTodoId}")
    @Operation(summary = "그룹 TO DO 삭제", description = "그룹 TO DO 삭제 요청 API")
    public ApiResponse<String> deleteGroupTodo(@PathVariable UUID groupTodoId,@PathVariable UUID groupId, @AuthenticationPrincipal CustomUserDetails user){
        groupTodoService.deleteGroupTodo(groupTodoId, groupId, user.getUserId());
        return ApiResponse.onDeleteSuccess("그룹 투두가 성공적으로 삭제되었습니다.");
    }
}
