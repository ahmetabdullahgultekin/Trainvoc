package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine.
 * Provides high-performance in-memory caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ROOMS_CACHE = "rooms";
    public static final String WORDS_CACHE = "words";
    public static final String LEADERBOARD_CACHE = "leaderboard";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setCacheNames(java.util.List.of(
                ROOMS_CACHE,
                WORDS_CACHE,
                LEADERBOARD_CACHE
        ));
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats();
    }
}
