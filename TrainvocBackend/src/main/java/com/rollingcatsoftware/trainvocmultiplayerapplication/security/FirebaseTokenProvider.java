package com.rollingcatsoftware.trainvocmultiplayerapplication.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Firebase token verification provider.
 * Verifies Firebase ID tokens and extracts user information.
 */
@Component
public class FirebaseTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenProvider.class);

    /**
     * Verifies a Firebase ID token.
     *
     * @param idToken Firebase ID token from client
     * @return Optional containing FirebaseToken if valid, empty otherwise
     */
    public Optional<FirebaseToken> verifyToken(String idToken) {
        if (!isFirebaseAvailable()) {
            log.debug("Firebase not available, cannot verify token");
            return Optional.empty();
        }

        try {
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return Optional.of(token);
        } catch (FirebaseAuthException e) {
            log.debug("Firebase token verification failed: {}", e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.debug("Invalid token format: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Extracts the Firebase UID from a token.
     *
     * @param token Verified Firebase token
     * @return Firebase UID
     */
    public String getUid(FirebaseToken token) {
        return token.getUid();
    }

    /**
     * Extracts the email from a token.
     *
     * @param token Verified Firebase token
     * @return Email address or null if not present
     */
    public String getEmail(FirebaseToken token) {
        return token.getEmail();
    }

    /**
     * Extracts the display name from a token.
     *
     * @param token Verified Firebase token
     * @return Display name or null if not present
     */
    public String getDisplayName(FirebaseToken token) {
        return token.getName();
    }

    /**
     * Checks if email is verified.
     *
     * @param token Verified Firebase token
     * @return true if email is verified
     */
    public boolean isEmailVerified(FirebaseToken token) {
        return token.isEmailVerified();
    }

    /**
     * Gets the sign-in provider (google.com, password, etc.).
     *
     * @param token Verified Firebase token
     * @return Sign-in provider identifier
     */
    public String getSignInProvider(FirebaseToken token) {
        Object provider = token.getClaims().get("firebase");
        if (provider instanceof java.util.Map<?, ?> firebaseClaims) {
            Object signInProvider = firebaseClaims.get("sign_in_provider");
            if (signInProvider != null) {
                return signInProvider.toString();
            }
        }
        return "unknown";
    }

    /**
     * Gets the picture URL from a token.
     *
     * @param token Verified Firebase token
     * @return Picture URL or null if not present
     */
    public String getPicture(FirebaseToken token) {
        return token.getPicture();
    }

    /**
     * Checks if Firebase is available and initialized.
     *
     * @return true if Firebase is available
     */
    public boolean isFirebaseAvailable() {
        return !FirebaseApp.getApps().isEmpty();
    }

    /**
     * Result of Firebase authentication.
     * Contains all relevant user information from the token.
     */
    public record FirebaseAuthResult(
            String uid,
            String email,
            String displayName,
            boolean emailVerified,
            String signInProvider,
            String pictureUrl
    ) {
        /**
         * Creates a FirebaseAuthResult from a verified token.
         */
        public static FirebaseAuthResult fromToken(FirebaseToken token, FirebaseTokenProvider provider) {
            return new FirebaseAuthResult(
                    provider.getUid(token),
                    provider.getEmail(token),
                    provider.getDisplayName(token),
                    provider.isEmailVerified(token),
                    provider.getSignInProvider(token),
                    provider.getPicture(token)
            );
        }
    }
}
