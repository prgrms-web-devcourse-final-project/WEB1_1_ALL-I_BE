package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.AllGroupTodoRes;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.controller.response.MyGroupTodosRes;
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

    @PostMapping("/{groupId}/todo")
    public ApiResponse<GroupTodoCreateRes> createGroupTodo(@RequestBody @Valid GroupTodoCreateReq req, @PathVariable UUID groupId){
        return ApiResponse.onCreateSuccess(groupTodoService.createGroupTodo(req, groupId));
    }

    @GetMapping("/{groupId}")
    public ApiResponse<AllGroupTodoRes> getGroupTodos (@PathVariable("groupId") UUID groupId, @RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(groupTodoService.getGroupTodos(groupId, user.getUserId(), year, month));
    }

    @GetMapping()
    public ApiResponse<MyGroupTodosRes> getMyGroupTodos(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        groupTodoService.getMyGroupTodos(user.getUserId(), year, month);
        return null;
    }
}
