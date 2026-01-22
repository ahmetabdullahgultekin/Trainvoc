package com.rollingcatsoftware.trainvocmultiplayerapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerRequest {
    private String roomCode;
    private String playerId;
    private String answer;
    private int answerTime;
    private int score;
    private boolean isCorrect;
    private double optionPickRate;

    public AnswerRequest() {
    }

    public static int calculateScore(boolean isCorrect, int answerTime, double optionPickRate, int maxTime) {
        int minScore = -50;
        // optionPickRate 0-1 aralığında değilse düzelt
        if (optionPickRate < 0) optionPickRate = 0;
        if (optionPickRate > 1) optionPickRate = 1;
        System.out.println("[SCORE] isCorrect: " + isCorrect + ", answerTime: " + answerTime + ", optionPickRate: " + optionPickRate + ", maxTime: " + maxTime);
        if (!isCorrect) {
            int rarityPenalty = (int) Math.round((1 - optionPickRate) * 30);
            int timePenalty = (int) Math.round(((double) answerTime / maxTime) * 20);
            int score = minScore + rarityPenalty + timePenalty;
            System.out.println("[SCORE] Yanlış cevap, score: " + score);
            return score;
        }
        int rarityBonus = (int) Math.round((1 - optionPickRate) * 30);
        int timeBonus = (int) Math.round((1 - ((double) answerTime / maxTime)) * 20);
        int score = Math.round(50 + rarityBonus + timeBonus);
        System.out.println("[SCORE] Doğru cevap, score: " + score);
        return score;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
