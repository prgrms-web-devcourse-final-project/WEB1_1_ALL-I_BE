package com.JAI.user.controller;


import com.JAI.global.controller.ApiResponse;
import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.controller.request.UserUpdateReq;
import com.JAI.user.controller.response.UserInfoRes;
import com.JAI.user.controller.response.UserSignupRes;
import com.JAI.user.controller.response.UserUpdateRes;
import com.JAI.user.jwt.JWTService;
import com.JAI.user.service.UserService;
import com.JAI.user.service.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;

    // 회원가입(이메일, 비밀번호, 닉네임(중복확인))
    @PostMapping("/signup")
    public ApiResponse<UserSignupRes> signup(@Valid @RequestBody UserSignupReq userSignupReq) {
        return ApiResponse.onSuccess( userService.signup(userSignupReq));
    }
    //회원 정보 수정(프로필 사진, 닉네임(중복확인), 마감시간)
    @PatchMapping("/myPage")
    public ApiResponse<UserUpdateRes> updateUserInfo(@Valid @RequestBody UserUpdateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(userService.updateUserInfo(req, user.getUserId()));
    }

    //회원 정보 조회(마이페이지)
    @GetMapping("/myPage")
    public ApiResponse<UserInfoRes> getUserInfo(@AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(userService.getUserInfo(user.getUserId()));
    }

    //액세스 토큰 재발급
    @PostMapping("/reissue")
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response){
        jwtService.reissue(request, response);
        return ApiResponse.onSuccess("Reissue Access Token Successfully");
    }

    @GetMapping("/{userId}")
    public ApiResponse<String> getUserNickname(@PathVariable UUID userId){
        return ApiResponse.onSuccess(userService.getUserNickname(userId));
    }

}
