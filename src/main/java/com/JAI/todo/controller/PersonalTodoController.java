package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.request.PersonalTodoStateReq;
import com.JAI.todo.controller.response.PersonalTodoListRes;
import com.JAI.todo.controller.response.PersonalTodoStateRes;
import com.JAI.todo.service.PersonalTodoService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class PersonalTodoController {
    private final PersonalTodoService personalTodoService;
    //투두 생성
    @PostMapping()
    public ApiResponse<String> createPersonalTodo(@RequestBody PersonalTodoCreateReq req, @AuthenticationPrincipal CustomUserDetails user){
        personalTodoService.createPersonalTodo(req, user);
        return ApiResponse.onCreateSuccess("Todo Create Success");
    }
    //투두 내용 수정
    @PatchMapping("/{todoId}/update")
    public ApiResponse<?> updatePersonalTodo(){

        return null;
    }

    //투두 완료
    @PatchMapping("/{todoId}/state")
    public ApiResponse<PersonalTodoStateRes> updatePersonTodoState(@PathVariable UUID todoId, @RequestBody PersonalTodoStateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.updatePersonTodoState(todoId, req, user));
    }
    //투두 삭제
    @DeleteMapping("/{todoId}")
    public ApiResponse<String> deletePersonalTodo(@PathVariable UUID todoId, @AuthenticationPrincipal CustomUserDetails user){
        personalTodoService.deletePersonalTodo(todoId, user);
        return ApiResponse.onDeleteSuccess("Todo Delete Success");
    }

    //투두 조회(한달치)
    @GetMapping
    public ApiResponse<List<PersonalTodoListRes>> getPersonalTodo(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        List<PersonalTodoListRes> responseList = personalTodoService.getTodoListForMonth(year, month, user);
        return ApiResponse.onSuccess(responseList);
    }

    //투두 조회(존재하는지만)

    //투두 조회(하루)

}
