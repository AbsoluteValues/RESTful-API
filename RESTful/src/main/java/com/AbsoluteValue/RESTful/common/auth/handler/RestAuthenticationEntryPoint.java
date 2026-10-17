package com.AbsoluteValue.RESTful.common.auth.handler;

import com.AbsoluteValue.RESTful.common.response.code.GlobalErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorWriter securityErrorWriter;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        securityErrorWriter.write(request, response, GlobalErrorCode.AUTHENTICATION_REQUIRED);
    }
}
