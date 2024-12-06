package com.JAI.user.service;

import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.controller.request.UserUpdateReq;
import com.JAI.user.controller.response.UserInfoRes;
import com.JAI.user.controller.response.UserSignupRes;
import com.JAI.user.controller.response.UserUpdateRes;
import com.JAI.user.domain.User;

import java.util.UUID;

public interface UserService {
    UserSignupRes signup(UserSignupReq userSignupReq);

    User getUserById(UUID userId);

    UserUpdateRes updateUserInfo(UserUpdateReq req, UUID userId);

    UserInfoRes getUserInfo(UUID userId);

    String getUserNickname(UUID userId);
}