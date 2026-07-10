package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_SUPER_ADMIN") || r.equals("ROLE_MANAGER") || r.equals("ROLE_STAFF"));

        if (isAdmin && request.getRequestURI().contains("/admin")) {
            response.sendRedirect("/admin");
        } else {
            String redirect = request.getParameter("redirect");
            response.sendRedirect(redirect != null && !redirect.isBlank() ? redirect : "/");
        }
    }
}
