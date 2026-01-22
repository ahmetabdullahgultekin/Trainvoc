package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the QUESTION state - players answering the current question.
 */
@Component
public class QuestionStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.QUESTION;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return Math.max(0, room.getQuestionDuration() - (int) elapsedSeconds);
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return room.getQuestionDuration();
    }

    @Override
    public GameState getNextState() {
        return GameState.ANSWER_REVEAL;
    }

    @Override
    public boolean hasAutomaticTransition() {
        return true;
    }
}
