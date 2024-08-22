package com.githubsalt.omoib.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubsalt.omoib.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        returnErrorResponse(response, authException);
    }

    private void returnErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse("Invalid token");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        log.error("timestamp: " + errorResponse.getTimestamp() + ", message: ", e);
    }
}
