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
        // Exclude /api/auth from filtering
        // lol imagion trying to get to the login page and then it requires a token to access. It's Like the experience to get a job issue.
        return path.startsWith("/api/auth");
    }
}
