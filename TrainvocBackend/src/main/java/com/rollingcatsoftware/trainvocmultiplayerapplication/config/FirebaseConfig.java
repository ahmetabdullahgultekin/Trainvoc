package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase Admin SDK configuration.
 * Initializes Firebase for server-side token verification.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path:classpath:firebase-service-account.json}")
    private Resource firebaseCredentials;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            log.info("Firebase authentication is disabled. Skipping Firebase initialization.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = buildFirebaseOptions();
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully");
            } catch (IOException e) {
                log.error("Failed to initialize Firebase Admin SDK: {}", e.getMessage());
                log.warn("Firebase authentication will not be available. " +
                        "Please ensure firebase-service-account.json is configured.");
            }
        } else {
            log.info("Firebase Admin SDK already initialized");
        }
    }

    private FirebaseOptions buildFirebaseOptions() throws IOException {
        FirebaseOptions.Builder builder = FirebaseOptions.builder();

        if (firebaseCredentials.exists()) {
            try (InputStream serviceAccount = firebaseCredentials.getInputStream()) {
                builder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
                log.info("Firebase credentials loaded from: {}", firebaseCredentials.getDescription());
            }
        } else {
            // Try default credentials (for GCP environments)
            builder.setCredentials(GoogleCredentials.getApplicationDefault());
            log.info("Using application default credentials for Firebase");
        }

        if (projectId != null && !projectId.isEmpty()) {
            builder.setProjectId(projectId);
        }

        return builder.build();
    }

    /**
     * Check if Firebase is properly initialized.
     */
    public boolean isFirebaseInitialized() {
        return firebaseEnabled && !FirebaseApp.getApps().isEmpty();
    }
}
