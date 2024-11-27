package com.JAI.user.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RedisTokenUtil redisTokenUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //로그아웃 요청인지 검증
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/user/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }
        //쿠키에서 토큰 추출
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        //추출한 토큰이 유효한지 검증
        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Refresh token not found in cookies");
            return;
        }

        //expired check
        try {
            if (jwtUtil.isExpired(refresh)) {
                throw new ExpiredJwtException(null, null, "RefreshToken is Expired");
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Refresh token is expired");
            return;
        }

        //추출한 토큰이 리프레시 토큰인지 검증
        String type = jwtUtil.getType(refresh);
        if (!type.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid token type: Not a refresh token");
            return;
        }
        //레디스에 저장되어 있는지 검증
        String email = jwtUtil.getEmail(refresh);
        String storedRefreshToken = redisTokenUtil.getRefreshToken(email);
        if(storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Refresh token does not exist or mismatch");
            return;
        }
        //로그아웃 진행
        //레디스에서 해당 토큰 값 삭제
        redisTokenUtil.deleteRefreshToken(email);
        //쿠키값 초기화해서 반환
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Logout successful");
    }
}
