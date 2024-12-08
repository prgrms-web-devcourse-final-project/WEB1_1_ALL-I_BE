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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
    @Operation(summary = "개인 TO DO 생성", description = "개인 TO DO 생성 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "그룹 TO DO", example = "개발 문서 작성"),
            @Parameter(name = "date", description = "그룹 TO DO 시작 날짜", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 TO DO 시작 시간", example = "21:00:00"),
            @Parameter(name = "categoryId", description = "TO DO 카테고리 아이디", example = "32eda718-fc6c-47ed-82ce-697c5f3f6b28")
    })
    public ApiResponse<String> createPersonalTodo(@RequestBody PersonalTodoCreateReq req, @AuthenticationPrincipal CustomUserDetails user){
        personalTodoService.createPersonalTodo(req, user);

        return ApiResponse.onCreateSuccess("Todo Create Success");
    }

    //투두 세부 항목 수정
    @PatchMapping("/{todoId}/update")
    @Operation(summary = "개인 TO DO 수정", description = "개인 TO DO 수정 요청 API")
    @Parameters({
            @Parameter(name = "title", description = "그룹 TO DO", example = "개발 문서 작성"),
            @Parameter(name = "date", description = "그룹 TO DO 시작 날짜", example = "2024-12-09"),
            @Parameter(name = "startTime", description = "그룹 TO DO 시작 시간", example = "21:00:00"),
            @Parameter(name = "categoryId", description = "TO DO 카테고리 아이디", example = "32eda718-fc6c-47ed-82ce-697c5f3f6b28")
    })
    public ApiResponse<PersonalTodoUpdateRes> updatePersonalTodo(@PathVariable UUID todoId, @RequestBody PersonalTodoUpdateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.updatePersonalTodo(todoId, req, user));
    }

    //투두 내용 변경
    @PatchMapping("/{todoId}/title")
    @Operation(summary = "개인 TO DO 제목 수정", description = "개인 TO DO 제목 수정 요청 API")
    @Parameter(name = "title", description = "그룹 TO DO", example = "개발 문서 완")
    public ApiResponse<PersonalTodoUpdateTitleRes> updatePersonalTodoTitle(@PathVariable UUID todoId, @RequestBody PersonalTodoUpdateTitleReq req, @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.onSuccess(personalTodoService.updatePersonalTodoTitle(todoId, req, user));
    }
    //투두 상태 변경
    @PatchMapping("/{todoId}/state")
    @Operation(summary = "개인 TO DO 달성으로 상태 변경 요청", description = "개인 TO DO 달성으로 상태 변경 요청 API")
    @Parameter(name = "state", description = "개인 TO DO 달성 여부", example = "true")
    public ApiResponse<PersonalTodoStateRes> updatePersonTodoState(@PathVariable UUID todoId, @RequestBody PersonalTodoStateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.updatePersonTodoState(todoId, req, user));
    }

    //투두 삭제
    @DeleteMapping("/{todoId}")
    @Operation(summary = "개인 TO DO 삭제 요청", description = "개인 TO DO 삭제 요청")
    public ApiResponse<String> deletePersonalTodo(@PathVariable UUID todoId, @AuthenticationPrincipal CustomUserDetails user){
        personalTodoService.deletePersonalTodo(todoId, user);
        return ApiResponse.onDeleteSuccess("Todo Delete Success");
    }

    //투두 조회(한달치)
    @GetMapping("/monthly")
    @Operation(summary = "개인 TO DO 한 달치 조회 요청", description = "개인 TO DO 한 달치 조회 요청")
    public ApiResponse<List<PersonalTodoRes>> getMonthlyPersonalTodoList(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        List<PersonalTodoRes> responseList = personalTodoService.getMonthlyPersonalTodoList(year, month, user);
        return ApiResponse.onSuccess(responseList);
    }

    //투두 조회(존재하는지만)
    @GetMapping("/exist-dates")
    @Operation(summary = "개인 TO DO 한 달치 간략한 조회 요청", description = "개인 TO DO 한 달치 간략한 조회 요청")
    public ApiResponse<List<PersonalTodoExistListRes>> getPersonalTodosExist(@RequestParam String year, @RequestParam String month, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.getPersonalTodosExist(year, month, user));
    }

    //투두 조회(하루)
    @GetMapping("/date")
    @Operation(summary = "개인 TO DO 하루치 조회 요청", description = "개인 TO DO 하루치 조회 요청")
    public ApiResponse<List<PersonalTodoRes>> getDailyPersonalTodoList(@RequestParam String year, @RequestParam String month,@RequestParam String day, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(personalTodoService.getDailyPersonalTodoList(year, month, day, user));
    }
}
