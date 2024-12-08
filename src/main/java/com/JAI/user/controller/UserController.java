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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    @Operation(summary = "회원 가입 & 기본(카테고리 1) 생성", description = "회원 가입 & 기본(카테고리 1) 생성 요청 API")
    @Parameters({
            @Parameter(name = "email", description = "사용자 이메일(NOT NULL)", example = "someone@gmail.com"),
            @Parameter(name = "password", description = "사용자 비밀번회(NOT NULL)", example = "abcd1234!"),
            @Parameter(name = "nickname", description = "닉네임(NOT NULL)", example = "주먹왕 랄프")
    })
    public ApiResponse<UserSignupRes> signup(@Valid @RequestBody UserSignupReq userSignupReq) {
        return ApiResponse.onSuccess( userService.signup(userSignupReq));
    }

    @Operation(summary = "회원 가입 & 기본(카테고리 1) 생성", description = "회원 가입 & 기본(카테고리 1) 생성 요청 API")
    @Parameters({
            @Parameter(name = "nickname", description = "닉네임(NOT NULL)", example = "주먹왕 랄프"),
            @Parameter(name = "imageUrl", description = "사용자 프로필 이미지", example = "http://abcd"),
            @Parameter(name = "endTime", description = "to do 실행 현황 알림 시간(NOT NULL)", example = "20:00")
    })
    //회원 정보 수정(프로필 사진, 닉네임(중복확인), 마감시간)
    @PatchMapping("/myPage")
    public ApiResponse<UserUpdateRes> updateUserInfo(@Valid @RequestBody UserUpdateReq req, @AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(userService.updateUserInfo(req, user.getUserId()));
    }

    //회원 정보 조회(마이페이지)
    @GetMapping("/myPage")
    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회 요청 API")
    public ApiResponse<UserInfoRes> getUserInfo(@AuthenticationPrincipal CustomUserDetails user){
        return ApiResponse.onSuccess(userService.getUserInfo(user.getUserId()));
    }

    //액세스 토큰 재발급
    @PostMapping("/reissue")
    @Operation(summary = "access token 재발급", description = "access token 재발급 요청 API")
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response){
        jwtService.reissue(request, response);
        return ApiResponse.onSuccess("Reissue Access Token Successfully");
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 id 조회", description = "사용자 id 조회 요청 API")
    public ApiResponse<String> getUserNickname(@PathVariable UUID userId){
        return ApiResponse.onSuccess(userService.getUserNickname(userId));
    }

}
