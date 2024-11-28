package com.JAI.user.service;

import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.domain.User;

import java.util.UUID;

public interface UserService {
    void signup(UserSignupReq userSignupReq);

    User getUserById(UUID userId);
}