package com.JAI.todo.controller;

import com.JAI.global.controller.ApiResponse;
import com.JAI.todo.service.PersonalTodoService;
import com.JAI.user.service.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/personal-todo")
public class PersonalTodoController {
}
