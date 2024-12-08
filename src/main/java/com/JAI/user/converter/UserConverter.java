package com.JAI.user.converter;

import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.controller.response.UserInfoRes;
import com.JAI.user.controller.response.UserSignupRes;
import com.JAI.user.controller.response.UserUpdateRes;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.UUID;

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

    public UserSignupRes toUserSignupDTO(UUID userId, String nickname, UUID categoryId){
        return UserSignupRes.builder()
                .userId(userId)
                .nickname(nickname)
                .categoryId(categoryId)
                .build();
    }

    public UserInfoRes toUserInfoDTO(User user){
        return UserInfoRes.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .endTime(user.getEndTime())
                .build();
    }

    public UserUpdateRes toUserUpdateDTO(User user){
        return UserUpdateRes.builder()
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .endTime(user.getEndTime())
                .build();
    }

    public UserDTO toUserDTO(User user){
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
