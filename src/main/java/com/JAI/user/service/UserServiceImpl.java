package com.JAI.user.service;

import com.JAI.global.controller.ApiResponse;
import com.JAI.user.controller.request.UserJoinReq;
import com.JAI.user.converter.UserConverter;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import com.JAI.user.jwt.JWTUtil;
import com.JAI.user.jwt.RedisTokenUtil;
import com.JAI.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisTokenUtil redisTokenUtil;
    private final UserConverter userConverter;

    @Override
    public void join(UserJoinReq userJoinReq) {

        //이메일 중복 확인
        if(userRepository.existsByEmail(userJoinReq.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        //닉네임 중복 확인
        if(userRepository.existsByNickname(userJoinReq.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        //Repo에 저장
        userRepository.save(userConverter.toUserEntity(userJoinReq));
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        //get Refresh Token
        String refresh = null;
        //쿠키에서 Refresh Token 추출
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        //RefreshToken이 없을때
        if(refresh == null) {
            //System.out.println("리프레시 토큰 없음");
            throw new IllegalArgumentException("refresh token null");
        }

        //RefreshToken 만료 여부 확인
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            //System.out.println("리프레시 토큰 만료됨");
            throw new IllegalArgumentException("refresh token expired");
        }

        //토큰이 refresh 토큰인지 확인
        String type = jwtUtil.getType(refresh);

        if(!type.equals("refresh")) {
            //System.out.println("리프레시 토큰이 아님");
            throw new IllegalArgumentException("invalid refresh token");
        }

        //검증된 refreshToken에서 이메일 받아오기
        String email = jwtUtil.getEmail(refresh);

        //해당 이메일의 토큰과 레디스에 저장되어 있는 토큰이 일치하는지 검증
        String storedRefreshToken = redisTokenUtil.getRefreshToken(email);
        if(storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            //System.out.println("레디스에 존재하지 않음");
            throw new IllegalArgumentException("Refresh token expired or mismatch");
        }

        //RefreshToken 검증 완 새 Access Token 발급
        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createJwt("access", email, role, 600000L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
    }
}
