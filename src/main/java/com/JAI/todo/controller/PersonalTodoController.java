package com.JAI.todo.controller;

import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/personal-todo")
public class PersonalTodoController {
    // TODO :: 로그인 권한 테스트용 코드
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getUserId());
        //System.out.println(userDetails.getUserId());
        return "Hello World";
    }
}
