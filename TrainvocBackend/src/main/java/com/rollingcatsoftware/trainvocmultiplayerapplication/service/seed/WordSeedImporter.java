package com.rollingcatsoftware.trainvocmultiplayerapplication.service.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Boots the words database from {@code classpath:seed/seed_v18.json} on application start.
 * <p>
 * The manifest is copied into the build resources from the client tree by a Gradle task
 * (single source of truth — no committed copy). It is deliberately <em>tolerant of an absent
 * manifest</em>: a backend jar built without the client tree (e.g. the Docker build context is
 * {@code ./TrainvocBackend} only) will not carry the resource, in which case this logs a
 * warning and skips rather than failing startup. When present, the manifest version is checked
 * hard (fail fast on mismatch) before the idempotent {@link WordSeedService} import runs.
 */
@Component
public class WordSeedImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WordSeedImporter.class);

    static final int EXPECTED_MANIFEST_VERSION = 1;
    static final int EXPECTED_DB_VERSION = 18;
    static final String DEFAULT_MANIFEST_PATH = "seed/seed_v18.json";

    private final WordSeedService seedService;
    private final ObjectMapper objectMapper;
    private final String manifestPath;

    public WordSeedImporter(WordSeedService seedService, ObjectMapper objectMapper) {
        this(seedService, objectMapper, DEFAULT_MANIFEST_PATH);
    }

    /** Test seam: lets tests point at a small fixture manifest on the test classpath. */
    WordSeedImporter(WordSeedService seedService, ObjectMapper objectMapper, String manifestPath) {
        this.seedService = seedService;
        this.objectMapper = objectMapper;
        this.manifestPath = manifestPath;
    }

    @Override
    public void run(ApplicationArguments args) {
        SeedManifest manifest = load();
        if (manifest == null) {
            return;
        }
        validateVersion(manifest);
        seedService.seedIfNeeded(manifest);
    }

    /** Reads and parses the manifest, or returns {@code null} if it is not on the classpath. */
    SeedManifest load() {
        ClassPathResource resource = new ClassPathResource(manifestPath);
        if (!resource.exists()) {
            log.warn("Seed manifest '{}' not found on the classpath — skipping words DB import. "
                    + "This is expected when the backend is built without the client tree "
                    + "(e.g. the Docker build context excludes ../TrainvocClient).", manifestPath);
            return null;
        }
        try (InputStream in = resource.getInputStream()) {
            return objectMapper.readValue(in, SeedManifest.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read seed manifest '" + manifestPath + "'", e);
        }
    }

    /** Fails fast if the manifest was produced for a different schema than this backend mirrors. */
    void validateVersion(SeedManifest manifest) {
        if (manifest.manifestVersion() != EXPECTED_MANIFEST_VERSION || manifest.dbVersion() != EXPECTED_DB_VERSION) {
            throw new IllegalStateException(String.format(
                    "Seed manifest version mismatch: expected manifestVersion=%d dbVersion=%d, got manifestVersion=%d dbVersion=%d",
                    EXPECTED_MANIFEST_VERSION, EXPECTED_DB_VERSION,
                    manifest.manifestVersion(), manifest.dbVersion()));
        }
    }
}
