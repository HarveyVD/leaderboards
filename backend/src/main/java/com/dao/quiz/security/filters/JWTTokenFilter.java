package com.dao.quiz.security.filters;


import com.dao.quiz.constants.SecurityConstants;
import com.dao.quiz.constants.WebConstants;
import com.dao.quiz.exceptions.NotAccessTokenException;
import com.dao.quiz.exceptions.WebException;
import com.dao.quiz.security.providers.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JWTTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(SecurityConstants.AUTH_TOKEN_HEADER_NAME);

        // If request is websocket type, we extract the auth token from param
        if (StringUtils.isBlank(token) && request.getServletPath().contains(WebConstants.API_WEBSOCKET_PREFIX)) {
            token = request.getParameter(SecurityConstants.AUTH_TOKEN_QUERY_PARAM);
        }

        if (StringUtils.isNotBlank(token)) {
            try {
                Authentication authentication = jwtTokenProvider.parseAccessToken(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (NotAccessTokenException e) {
                throw WebException.unauthorized(e.getMessage());
            } catch (Exception e) {
                throw WebException.unauthorized("Authentication is required");
            }
        }

        filterChain.doFilter(request, response);
    }
}
