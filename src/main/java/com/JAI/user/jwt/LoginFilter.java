package com.JAI.user.jwt;

import com.JAI.user.controller.request.UserLoginReq;
import com.JAI.user.domain.User;
import com.JAI.user.repository.UserRepository;
import com.JAI.user.service.dto.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

//스프링 시큐리티의 Form 로그인을 disable 시켰기때문에 커스텀으로 만들어줘야됨
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisTokenUtil redisTokenUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RedisTokenUtil redisTokenUtil) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/user/login");
        this.jwtUtil = jwtUtil;
        this.redisTokenUtil = redisTokenUtil;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            UserLoginReq userLoginReq = new ObjectMapper().readValue(req.getInputStream(), UserLoginReq.class);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userLoginReq.getEmail(), userLoginReq.getPassword(), null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String access = jwtUtil.createJwt("access", email, role, 3600000L);
        String refresh = jwtUtil.createJwt("refresh", email, role, 86400000L);

        redisTokenUtil.saveRefreshToken(email, refresh, 86400); // Redis는 초 단위 TTL

        //response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        // 응답 본문에 JSON으로 데이터 추가 (ok 메시지와 함께 토큰값)
        String jsonResponse = "{"
                + "\"message\": \"ok\","
                + "\"access_token\": \"" + "Bearer " + access + "\","
                + "\"refresh_token\": \"" + refresh + "\""
                + "}";
        // TODO :: deveolp 올릴때 아래 껄로
//                String jsonResponse = "{"
//                + "\"message\": \"ok\","
//                + "\"access_token\": \"" + access + "\","
//                + "\"refresh_token\": \"" + refresh + "\""
//                + "}";

        // 응답 본문에 JSON을 작성하여 보내기
        try {
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(86400); // 1일
        cookie.setHttpOnly(true);
        return cookie;
    }
}
