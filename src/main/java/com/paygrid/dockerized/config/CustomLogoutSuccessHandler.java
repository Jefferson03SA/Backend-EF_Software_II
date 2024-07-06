package com.paygrid.dockerized.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        if (authentication != null) {
            response.getWriter().write("{\"message\": \"Has cerrado sesión exitosamente.\"}");
        } else {
            response.getWriter().write("{\"message\": \"No hay una sesión activa.\"}");
        }

        response.getWriter().flush();
    }
}
