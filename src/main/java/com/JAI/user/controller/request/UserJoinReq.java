package com.JAI.user.controller.request;

public record UserJoinReq(
        String email,
        String password,
        String nickname
) {
}
