package com.JAI.user.jwt;

import com.JAI.user.domain.Role;
import com.JAI.user.domain.User;
import com.JAI.user.service.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 access키에 담긴 access token 추출
        String accessToken = request.getHeader("access");

        //토큰이 없으면 다음 필터로
        if(accessToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 만료 여부 확인, 만료시 예외
        try{
            jwtUtil.isExpired(accessToken);
        }catch(ExpiredJwtException e){
            //예외처리 메세지 작성
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //access 토큰인지 확인
        String type = jwtUtil.getType(accessToken);

        if(!type.equals("access")){
            //예외처리 메세지 작성
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        //토큰에서 username과 role 획득
        String email = jwtUtil.getEmail(accessToken);
        String roleString = jwtUtil.getRole(accessToken);

        Role role = Role.valueOf(roleString);

        //userEntity를 생성하여 값 set
        User userEntity = User.createLoginInfo(email, role);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 로그인 검증 및 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        //다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
