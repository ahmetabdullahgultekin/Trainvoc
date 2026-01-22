package com.rollingcatsoftware.trainvocmultiplayerapplication.util;

/**
 * Calculates player scores based on answer correctness, timing, and rarity.
 */
public final class ScoreCalculator {

    // Score constants
    private static final int MIN_SCORE = -50;
    private static final int BASE_CORRECT_SCORE = 50;
    private static final int MAX_RARITY_BONUS = 30;
    private static final int MAX_TIME_BONUS = 20;

    private ScoreCalculator() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculates the score for an answer.
     *
     * @param isCorrect whether the answer was correct
     * @param answerTimeMs time taken to answer in milliseconds
     * @param optionPickRate how often this option was picked (0-1)
     * @param maxTimeMs maximum allowed time in milliseconds
     * @return calculated score (positive for correct, negative for incorrect)
     */
    public static int calculate(boolean isCorrect, int answerTimeMs, double optionPickRate, int maxTimeMs) {
        // Clamp optionPickRate to valid range
        double clampedPickRate = Math.max(0, Math.min(1, optionPickRate));
        double timeRatio = maxTimeMs > 0 ? (double) answerTimeMs / maxTimeMs : 0;

        int rarityComponent = (int) Math.round((1 - clampedPickRate) * MAX_RARITY_BONUS);
        int timeComponent = (int) Math.round((1 - timeRatio) * MAX_TIME_BONUS);

        if (!isCorrect) {
            // Penalty: base penalty + reduced bonuses as penalties
            return MIN_SCORE + rarityComponent + (MAX_TIME_BONUS - timeComponent);
        }

        // Reward: base score + rarity bonus + time bonus
        return BASE_CORRECT_SCORE + rarityComponent + timeComponent;
    }
}
