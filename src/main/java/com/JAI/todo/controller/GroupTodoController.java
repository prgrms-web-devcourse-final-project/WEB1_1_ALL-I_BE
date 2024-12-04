package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.controller.request.GroupTodoCreateReq;
import com.JAI.todo.controller.response.GroupTodoCreateRes;
import com.JAI.todo.service.GroupTodoService;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
