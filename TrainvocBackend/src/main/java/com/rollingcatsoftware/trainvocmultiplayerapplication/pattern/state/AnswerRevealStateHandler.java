package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the ANSWER_REVEAL state - showing correct answer.
 * Host manually triggers transition to next question.
 */
@Component
public class AnswerRevealStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.ANSWER_REVEAL;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return 0; // No time limit - host controls transition
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return 0; // No automatic duration
    }

    @Override
    public GameState getNextState() {
        return null; // Manual transition only
    }

    @Override
    public boolean hasAutomaticTransition() {
        return false;
    }
}
