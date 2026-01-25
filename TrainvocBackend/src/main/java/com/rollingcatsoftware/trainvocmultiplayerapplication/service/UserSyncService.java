package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.AuthProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.UserRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.FirebaseTokenProvider.FirebaseAuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for syncing Firebase users to the local database.
 * Creates or updates local user records based on Firebase authentication.
 */
@Service
public class UserSyncService {

    private static final Logger log = LoggerFactory.getLogger(UserSyncService.class);

    private final UserRepository userRepository;

    public UserSyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Syncs a Firebase user to the local database.
     * Creates a new user if not exists, or updates existing user.
     *
     * @param authResult Firebase authentication result
     * @return The synced local User entity
     */
    @Transactional
    public User syncFirebaseUser(FirebaseAuthResult authResult) {
        // First, try to find by Firebase UID
        Optional<User> existingByUid = userRepository.findByFirebaseUid(authResult.uid());

        if (existingByUid.isPresent()) {
            return updateExistingUser(existingByUid.get(), authResult);
        }

        // If not found by UID, check if email exists (linking existing account)
        if (authResult.email() != null) {
            Optional<User> existingByEmail = userRepository.findByEmailIgnoreCase(authResult.email());
            if (existingByEmail.isPresent()) {
                return linkFirebaseToExistingUser(existingByEmail.get(), authResult);
            }
        }

        // Create new user
        return createNewFirebaseUser(authResult);
    }

    /**
     * Updates an existing user's information from Firebase.
     */
    private User updateExistingUser(User user, FirebaseAuthResult authResult) {
        log.debug("Updating existing user: {} (Firebase UID: {})", user.getUsername(), authResult.uid());

        // Update email verification status
        user.setEmailVerified(authResult.emailVerified());

        // Update display name if provided and current is empty
        if (authResult.displayName() != null && user.getDisplayName() == null) {
            user.setDisplayName(authResult.displayName());
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Links Firebase authentication to an existing local user.
     * This happens when a user who registered locally signs in with Firebase.
     */
    private User linkFirebaseToExistingUser(User user, FirebaseAuthResult authResult) {
        log.info("Linking Firebase UID to existing user: {} (email: {})",
                user.getUsername(), authResult.email());

        user.setFirebaseUid(authResult.uid());
        user.setEmailVerified(authResult.emailVerified());

        // Update auth provider based on sign-in method
        AuthProvider provider = determineAuthProvider(authResult.signInProvider());
        user.setAuthProvider(provider);

        // Update display name if provided and current is null
        if (authResult.displayName() != null && user.getDisplayName() == null) {
            user.setDisplayName(authResult.displayName());
        }

        user.setLastLogin(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Creates a new user from Firebase authentication.
     */
    private User createNewFirebaseUser(FirebaseAuthResult authResult) {
        log.info("Creating new user from Firebase: email={}, provider={}",
                authResult.email(), authResult.signInProvider());

        User user = new User();

        // Set Firebase UID
        user.setFirebaseUid(authResult.uid());

        // Set email
        user.setEmail(authResult.email() != null ? authResult.email() : generatePlaceholderEmail(authResult.uid()));

        // Generate username from display name or UID
        String username = generateUsername(authResult);
        user.setUsername(username);

        // Set display name
        user.setDisplayName(authResult.displayName() != null ? authResult.displayName() : username);

        // Set a placeholder password (not used for Firebase auth)
        user.setPassword(generateSecureRandomPassword());

        // Set auth provider
        user.setAuthProvider(determineAuthProvider(authResult.signInProvider()));

        // Set email verification status
        user.setEmailVerified(authResult.emailVerified());

        // Set last login
        user.setLastLogin(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Generates a unique username from the Firebase auth result.
     */
    private String generateUsername(FirebaseAuthResult authResult) {
        // Try display name first
        if (authResult.displayName() != null && !authResult.displayName().isEmpty()) {
            String baseUsername = authResult.displayName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "");

            if (baseUsername.length() >= 3) {
                return ensureUniqueUsername(baseUsername);
            }
        }

        // Try email local part
        if (authResult.email() != null && authResult.email().contains("@")) {
            String baseUsername = authResult.email()
                    .split("@")[0]
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "");

            if (baseUsername.length() >= 3) {
                return ensureUniqueUsername(baseUsername);
            }
        }

        // Fall back to "user" + random suffix
        return ensureUniqueUsername("user");
    }

    /**
     * Ensures the username is unique by appending numbers if needed.
     */
    private String ensureUniqueUsername(String baseUsername) {
        // Truncate to reasonable length
        if (baseUsername.length() > 20) {
            baseUsername = baseUsername.substring(0, 20);
        }

        String username = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsernameIgnoreCase(username)) {
            username = baseUsername + suffix;
            suffix++;

            // Safety limit
            if (suffix > 10000) {
                username = baseUsername + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        }

        return username;
    }

    /**
     * Determines the AuthProvider from the Firebase sign-in provider string.
     */
    private AuthProvider determineAuthProvider(String signInProvider) {
        if (signInProvider == null) {
            return AuthProvider.FIREBASE;
        }

        return switch (signInProvider) {
            case "google.com" -> AuthProvider.GOOGLE;
            case "password" -> AuthProvider.FIREBASE;
            default -> AuthProvider.FIREBASE;
        };
    }

    /**
     * Generates a placeholder email for users without email.
     */
    private String generatePlaceholderEmail(String uid) {
        return uid + "@firebase.local";
    }

    /**
     * Generates a secure random password for Firebase users.
     * This password is never used since Firebase handles authentication.
     */
    private String generateSecureRandomPassword() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    /**
     * Finds a user by their Firebase UID.
     *
     * @param firebaseUid Firebase UID
     * @return Optional containing the user if found
     */
    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
}
