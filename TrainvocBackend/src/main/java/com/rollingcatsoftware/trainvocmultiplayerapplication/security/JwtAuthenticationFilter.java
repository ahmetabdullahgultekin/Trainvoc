package com.rollingcatsoftware.trainvocmultiplayerapplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter.
 * Intercepts requests and validates JWT tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String token = jwtTokenProvider.resolveToken(bearerToken);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Check if this is a user token or a player token
            if (jwtTokenProvider.isUserToken(token)) {
                // User authentication token
                Long userId = jwtTokenProvider.getUserId(token);
                String username = jwtTokenProvider.getUsername(token);
                Set<String> roles = jwtTokenProvider.getRoles(token);

                var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                UserAuthentication auth = new UserAuthentication(userId, username, roles);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(auth, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Player/game authentication token
                String playerId = jwtTokenProvider.getPlayerId(token);
                String roomCode = jwtTokenProvider.getRoomCode(token);

                PlayerAuthentication auth = new PlayerAuthentication(playerId, roomCode);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(auth, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Custom authentication object holding player context (for game sessions).
     */
    public record PlayerAuthentication(String playerId, String roomCode) {
    }

    /**
     * Custom authentication object holding user context (for registered users).
     */
    public record UserAuthentication(Long userId, String username, Set<String> roles) {
    }
}
