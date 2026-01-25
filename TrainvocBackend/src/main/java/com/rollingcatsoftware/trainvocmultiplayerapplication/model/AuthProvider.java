package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

/**
 * Authentication provider types.
 * Indicates how a user was authenticated.
 */
public enum AuthProvider {
    /**
     * Local authentication (email/password via custom backend).
     */
    LOCAL,

    /**
     * Firebase authentication (email/password, Google, etc.).
     */
    FIREBASE,

    /**
     * Google Sign-In via Firebase.
     */
    GOOGLE
}
