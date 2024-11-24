package com.JAI.user.controller.request;

public record UserLoginReq(
        String email,
        String password
) {
}
