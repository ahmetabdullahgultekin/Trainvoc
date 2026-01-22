package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

/**
 * Centralized constants for game configuration.
 * Addresses magic number issues and provides a single source of truth.
 */
public final class GameConstants {

    private GameConstants() {
        // Prevent instantiation
    }

    // Game timing constants (in seconds)
    public static final int COUNTDOWN_SECONDS = 3;
    public static final int DEFAULT_QUESTION_DURATION = 60;
    public static final int ANSWER_REVEAL_SECONDS = 0;
    public static final int RANKING_SECONDS = 10;

    // Scoring constants
    public static final int MIN_SCORE = -50;
    public static final int BASE_CORRECT_SCORE = 50;
    public static final int RARITY_BONUS_MAX = 30;
    public static final int TIME_BONUS_MAX = 20;

    // Room code generation
    public static final int ROOM_CODE_LENGTH = 5;

    // Avatar options
    public static final String[] ALLOWED_AVATARS = {
            "🦊", "🐱", "🐶", "🐵", "🐸", "🐼", "🐧", "🐯", "🦁", "🐮",
            "🐨", "🐰", "🐻", "🐷", "🐔", "🦄", "🐙", "🐢", "🐳", "🐝"
    };

    public static int getAvatarCount() {
        return ALLOWED_AVATARS.length;
    }

    public static boolean isValidAvatarId(Integer avatarId) {
        return avatarId != null && avatarId >= 0 && avatarId < ALLOWED_AVATARS.length;
    }

    public static String getAvatar(int avatarId) {
        if (isValidAvatarId(avatarId)) {
            return ALLOWED_AVATARS[avatarId];
        }
        return ALLOWED_AVATARS[0];
    }
}
