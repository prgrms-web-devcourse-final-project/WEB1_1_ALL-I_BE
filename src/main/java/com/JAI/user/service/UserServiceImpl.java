package com.JAI.user.service;

import com.JAI.global.controller.ApiResponse;
import com.JAI.user.controller.request.UserJoinReq;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import com.JAI.user.jwt.JWTUtil;
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

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public void join(UserJoinReq userJoinReq) {

        String email = userJoinReq.email();
        String password = userJoinReq.password();
        String nickname = userJoinReq.nickname();

        //이메일 중복 확인
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        //닉네임 중복 확인 --> 이걸 예외 처리하는게 맞나?
        if(userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        //비밀번호 암호화?
        User user = User.create(
                nickname,
                email,
                bCryptPasswordEncoder.encode(password),
                Provider.ORIGIN
        );
        //Repo에 저장
        userRepository.save(user);
    }

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        //get Refresh Token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null) {
            throw new IllegalArgumentException("refresh token null");
        }

        //만료 체크
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            throw new IllegalArgumentException("refresh token expired");
        }

        //토큰이 refresh 토큰인지 확인
        String type = jwtUtil.getType(refresh);

        if(!type.equals("refresh")) {
            throw new IllegalArgumentException("invalid refresh token");
        }

        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        //새 Access Token 발급
        String newAccess = jwtUtil.createJwt("access", email, role, 600000L);

        //response
        response.setHeader("access", newAccess);
    }
}
