package com.JAI.user.service;

import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.converter.UserConverter;
import com.JAI.user.jwt.JWTUtil;
import com.JAI.user.jwt.RedisTokenUtil;
import com.JAI.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public void signup(UserSignupReq userSignupReq) {

        //이메일 중복 확인
        if(userRepository.existsByEmail(userSignupReq.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        //닉네임 중복 확인
        if(userRepository.existsByNickname(userSignupReq.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        //Repo에 저장
        userRepository.save(userConverter.toUserEntity(userSignupReq));
    }


}
