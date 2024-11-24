package com.JAI.user.controller;


import com.JAI.global.controller.ApiResponse;
import com.JAI.user.controller.request.UserJoinReq;
import com.JAI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입(이메일, 비밀번호, 닉네임(중복확인))
    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody UserJoinReq userJoinReq) {
        System.out.println("Post Controller");
        userService.join(userJoinReq);
        return ApiResponse.ok("Success");//(HttpStatus.CREATED, "success");
    }
    //회원 정보 수정(프로필 사진, 닉네임(중복확인), 마감시간)
    @PatchMapping("")
    public ApiResponse<?> updateUserInfo(){
        return null;
    }

    //회원 정보 조회(마이페이지)
    //프로필 사진, 닉네임, 이메일
    @GetMapping("/info")
    public ApiResponse<String> getUserInfo(){
        System.out.println("Get Controller");
        return ApiResponse.ok("Success");
    }


}
