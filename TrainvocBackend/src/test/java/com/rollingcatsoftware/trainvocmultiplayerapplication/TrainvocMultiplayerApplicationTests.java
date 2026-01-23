package com.rollingcatsoftware.trainvocmultiplayerapplication;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test that verifies the full Spring context loads successfully.
 * Requires PostgreSQL database to be running.
 *
 * Run with: ./gradlew test -PincludeIntegrationTests
 * Or run only unit tests: ./gradlew test (default)
 */
@SpringBootTest
@Tag("integration")
class TrainvocMultiplayerApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies the Spring context loads with all beans wired correctly
        // Requires PostgreSQL databases: trainvoc and trainvoc-words
    }

}
