package com.JAI.user.service;

import com.JAI.user.controller.request.UserSignupReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void signup(UserSignupReq userSignupReq);
}
