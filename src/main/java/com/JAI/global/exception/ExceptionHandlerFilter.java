package com.JAI.global.exception;

import com.JAI.global.controller.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
//@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response); // 요청을 컨트롤러로 전달
        } catch (Exception e) { // 모든 예외 처리
            log.error("Error during filtering: {}", e.getMessage());
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType("application/json");
        ApiResponse<Object> apiResponse = ApiResponse.of(status, ex.getMessage(), null);

        try {
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
            response.getWriter().flush(); // 버퍼 비우기
            response.getWriter().close(); // 스트림 닫기
        } catch (IOException e) {
            log.error("Error writing response: {}", e.getMessage());
        }
    }
}

