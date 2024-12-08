package com.JAI.user.jwt;

import com.JAI.user.domain.User;
import com.JAI.user.exception.*;
import com.JAI.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RedisTokenUtil redisTokenUtil;


    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

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
            throw new RefreshTokenNotFoundException("refresh token null");
        }

        //RefreshToken 만료 여부 확인
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            //System.out.println("리프레시 토큰 만료됨");
            throw new RefreshTokenExpiredException("refresh token expired",e);
        }

        //토큰이 refresh 토큰인지 확인
        String type = jwtUtil.getType(refresh);

        if(!type.equals("refresh")) {
            //System.out.println("리프레시 토큰이 아님");
            throw new InvalidTokenTypeException("invalid refresh token");
        }

        //검증된 refreshToken에서 이메일 받아오기
        String email = jwtUtil.getEmail(refresh);

        //해당 이메일의 토큰과 레디스에 저장되어 있는 토큰이 일치하는지 검증
        String storedRefreshToken = redisTokenUtil.getRefreshToken(email);
        if(storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            //System.out.println("레디스에 존재하지 않음");
            throw new RefreshTokenMismatchException("Refresh token expired or mismatch");
        }

        //RefreshToken 검증 완 새 Access Token 발급
        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createJwt("access", email, role, 600000L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
    }
}
