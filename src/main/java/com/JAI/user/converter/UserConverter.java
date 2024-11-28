package com.JAI.user.converter;

import com.JAI.user.controller.request.UserJoinReq;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User toUserEntity(UserJoinReq requset){
        User user = User.create(
                requset.getNickname(),
                requset.getEmail(),
                bCryptPasswordEncoder.encode(requset.getPassword()),
                Provider.ORIGIN
        );

        return user;
    }
}
