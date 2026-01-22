package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the FINAL state - game has ended.
 */
@Component
public class FinalStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.FINAL;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return 0; // Game is over
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return 0;
    }

    @Override
    public GameState getNextState() {
        return null; // Terminal state
    }

    @Override
    public boolean hasAutomaticTransition() {
        return false;
    }
}
