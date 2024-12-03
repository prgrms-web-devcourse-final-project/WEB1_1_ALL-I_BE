package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.controller.request.PersonalTodoCreateReq;
import com.JAI.todo.controller.request.PersonalTodoStateReq;
import com.JAI.todo.controller.request.PersonalTodoUpdateReq;
import com.JAI.todo.controller.request.PersonalTodoUpdateTitleReq;
import com.JAI.todo.controller.response.*;
import com.JAI.todo.domain.PersonalTodo;
import com.JAI.todo.service.PersonalTodoService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    //투두 세부 항목 수정
    @PatchMapping("/{todoId}/update")
    public ApiResponse<PersonalTodoUpdateRes> updatePersonalTodo(@PathVariable UUID todoId, @RequestBody PersonalTodoUpdateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.updatePersonalTodo(todoId, req, user));
    }

    //투두 내용 변경
    @PatchMapping("/{todoId}/title")
    public ApiResponse<PersonalTodoUpdateTitleRes> updatePersonalTodoTitle(@PathVariable UUID todoId, @RequestBody PersonalTodoUpdateTitleReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(personalTodoService.updatePersonalTodoTitle(todoId, req, user));
    }
    //투두 상태 변경
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
    @GetMapping("/monthly")
    public ApiResponse<List<PersonalTodoRes>> getMonthlyPersonalTodoList(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        List<PersonalTodoRes> responseList = personalTodoService.getMonthlyPersonalTodoList(year, month, user);
        return ApiResponse.onSuccess(responseList);
    }

    //투두 조회(존재하는지만)
    @GetMapping("/exist-dates")
    public ApiResponse<List<PersonalTodoExistListRes>> getPersonalTodosExist(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.getPersonalTodosExist(year, month, user));
    }

    //투두 조회(하루)
    @GetMapping("/date")
    public ApiResponse<List<PersonalTodoRes>> getDailyPersonalTodoList(@RequestParam String year, @RequestParam String month,@RequestParam String day, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.getDailyPersonalTodoList(year, month, day, user));
    }
}
