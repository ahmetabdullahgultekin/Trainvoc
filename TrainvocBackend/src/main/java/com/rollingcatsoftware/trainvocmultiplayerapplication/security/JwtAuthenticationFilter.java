package com.rollingcatsoftware.trainvocmultiplayerapplication.security;

import com.google.firebase.auth.FirebaseToken;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.UserSyncService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter.
 * Intercepts requests and validates JWT tokens.
 * Supports both local JWT and Firebase ID tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final FirebaseTokenProvider firebaseTokenProvider;
    private final UserSyncService userSyncService;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            FirebaseTokenProvider firebaseTokenProvider,
            UserSyncService userSyncService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.firebaseTokenProvider = firebaseTokenProvider;
        this.userSyncService = userSyncService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String token = jwtTokenProvider.resolveToken(bearerToken);

        if (token != null) {
            // Try Firebase token verification first (if Firebase is available)
            if (firebaseTokenProvider.isFirebaseAvailable() && tryFirebaseAuthentication(token)) {
                // Firebase authentication succeeded
                filterChain.doFilter(request, response);
                return;
            }

            // Fall back to local JWT validation
            if (jwtTokenProvider.validateToken(token)) {
                handleLocalJwtAuthentication(token);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Attempts to authenticate using Firebase ID token.
     *
     * @param token The token to verify
     * @return true if Firebase authentication succeeded
     */
    private boolean tryFirebaseAuthentication(String token) {
        Optional<FirebaseToken> firebaseToken = firebaseTokenProvider.verifyToken(token);

        if (firebaseToken.isEmpty()) {
            return false;
        }

        FirebaseToken fbToken = firebaseToken.get();
        log.debug("Firebase token verified for UID: {}", fbToken.getUid());

        // Build Firebase auth result
        FirebaseTokenProvider.FirebaseAuthResult authResult =
                FirebaseTokenProvider.FirebaseAuthResult.fromToken(fbToken, firebaseTokenProvider);

        // Sync user to local database
        User user = userSyncService.syncFirebaseUser(authResult);

        // Create authentication object
        Set<String> roles = user.getRoles().stream()
                .map(User.Role::name)
                .collect(Collectors.toSet());

        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        FirebaseAuthentication auth = new FirebaseAuthentication(
                user.getId(),
                user.getUsername(),
                fbToken.getUid(),
                roles
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(auth, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Firebase authentication set for user: {} (ID: {})", user.getUsername(), user.getId());

        return true;
    }

    /**
     * Handles local JWT authentication (existing flow).
     *
     * @param token The validated JWT token
     */
    private void handleLocalJwtAuthentication(String token) {
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

    /**
     * Custom authentication object holding Firebase user context.
     */
    public record FirebaseAuthentication(Long userId, String username, String firebaseUid, Set<String> roles) {
    }
}
