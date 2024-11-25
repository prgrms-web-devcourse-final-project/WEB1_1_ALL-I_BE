package com.JAI.user.service;

import com.JAI.global.controller.ApiResponse;
import com.JAI.user.controller.request.UserJoinReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    void join(UserJoinReq userJoinReq);
    void reissue(HttpServletRequest request, HttpServletResponse response);
}
