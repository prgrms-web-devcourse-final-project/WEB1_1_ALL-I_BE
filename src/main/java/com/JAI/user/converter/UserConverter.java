package com.JAI.user.converter;

import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User toUserEntity(UserSignupReq requset){
        User user = User.create(
                requset.getNickname(),
                requset.getEmail(),
                bCryptPasswordEncoder.encode(requset.getPassword()),
                Provider.ORIGIN
        );

        return user;
    }
}
