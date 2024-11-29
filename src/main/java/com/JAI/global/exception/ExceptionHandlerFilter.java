//package com.JAI.global.exception;
//
//import com.JAI.global.controller.ApiResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class ExceptionHandlerFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try{
//            filterChain.doFilter(request, response);
//        } catch (JwtException e){
//            log.error(e.getMessage());
//            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
//        }
//    }
//
//    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex){
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.setStatus(status.value());
//        response.setContentType("application/json");
//        ApiResponse apiResponse =  ApiResponse.of(
//                HttpStatus.UNAUTHORIZED,
//                ex.getMessage(),
//                null
//        );
//        try{
//            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write(jsonResponse);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//}
