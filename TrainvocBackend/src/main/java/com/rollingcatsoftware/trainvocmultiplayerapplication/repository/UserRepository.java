package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 * Provides methods for user authentication and management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (case-insensitive).
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by email (case-insensitive).
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Find user by username or email (for login).
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:identifier) OR LOWER(u.email) = LOWER(:identifier)")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Check if username exists.
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if email exists.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Update last login timestamp.
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Find top users by total score (leaderboard).
     */
    List<User> findTop100ByOrderByTotalScoreDesc();

    /**
     * Find top users by games won.
     */
    List<User> findTop100ByOrderByGamesWonDesc();

    /**
     * Find users who joined after a specific date.
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Count active users (logged in within last 30 days).
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin > :since")
    long countActiveUsersSince(@Param("since") LocalDateTime since);

    /**
     * Find users by role.
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") User.Role role);
}
