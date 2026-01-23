package com.rollingcatsoftware.trainvocmultiplayerapplication.security;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for authentication.
 * Handles token generation and validation.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret:defaultSecretKeyThatShouldBeChangedInProduction123456}") String secret,
            @Value("${jwt.expiration:86400000}") long validityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * Creates a JWT token for the given player.
     */
    public String createToken(String playerId, String playerName, String roomCode) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(playerId)
                .claim("name", playerName)
                .claim("room", roomCode)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the player ID from a token.
     */
    public String getPlayerId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extracts the room code from a token.
     */
    public String getRoomCode(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("room", String.class);
    }

    /**
     * Validates a token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts token from Authorization header.
     */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // ============ User Authentication Methods ============

    /**
     * Creates a JWT token for authenticated user.
     *
     * @param userId User's database ID
     * @param username User's username
     * @param roles User's roles
     * @return JWT token string
     */
    public String createUserToken(Long userId, String username, Set<User.Role> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String roleString = roles.stream()
                .map(User.Role::name)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", roleString)
                .claim("type", "user")
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts user ID from a user token.
     *
     * @param token JWT token
     * @return User ID
     */
    public Long getUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Extracts username from a token.
     *
     * @param token JWT token
     * @return Username
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    /**
     * Extracts roles from a token.
     *
     * @param token JWT token
     * @return Set of role names
     */
    public Set<String> getRoles(String token) {
        String rolesString = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", String.class);

        if (rolesString == null || rolesString.isEmpty()) {
            return Set.of();
        }

        return Set.of(rolesString.split(","));
    }

    /**
     * Checks if the token is a user authentication token.
     *
     * @param token JWT token
     * @return true if it's a user token
     */
    public boolean isUserToken(String token) {
        try {
            String type = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);
            return "user".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}
