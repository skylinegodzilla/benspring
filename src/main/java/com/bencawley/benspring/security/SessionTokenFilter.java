package com.bencawley.benspring.security;
// OK note to self security is way over my head. Just stick with the study material and pray that it works

import com.bencawley.benspring.services.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionTokenFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    public SessionTokenFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("X-Session-Token");
        if (token == null || sessionService.validateSession(token) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing session token");
            return; // block request if invalid token
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // 1) Allow unauthenticated access to your login/register endpoints
        if (path.startsWith("/api/auth")) {
            return true;
        }

        // 2) Allow the root landing page
        if ("/".equals(path)) {
            return true;
        }

        // 3) Allow Spring Boot’s error page
        if (path.startsWith("/error")) {
            return true;
        }

        // 4) Allow any static resources (css, js, images, etc.)
        if (path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.startsWith("/webjars")) {
            return true;
        }

        // 5) Swagger
        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources/**")
                || path.startsWith("/webjars/**")) {
             return true;
        }

        // everything else *will* be filtered
        return false;
    }
}
