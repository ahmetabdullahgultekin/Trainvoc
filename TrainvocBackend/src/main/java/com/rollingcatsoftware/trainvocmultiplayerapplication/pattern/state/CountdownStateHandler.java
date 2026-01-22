package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the COUNTDOWN state - brief countdown before question appears.
 */
@Component
public class CountdownStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.COUNTDOWN;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return Math.max(0, GameConstants.COUNTDOWN_SECONDS - (int) elapsedSeconds);
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return GameConstants.COUNTDOWN_SECONDS;
    }

    @Override
    public GameState getNextState() {
        return GameState.QUESTION;
    }

    @Override
    public boolean hasAutomaticTransition() {
        return true;
    }
}
