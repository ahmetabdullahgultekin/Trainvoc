package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the LOBBY state - waiting for players and game start.
 */
@Component
public class LobbyStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.LOBBY;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return 0; // No time limit in lobby
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return 0; // No time limit
    }

    @Override
    public GameState getNextState() {
        return null; // Manual transition only (host starts game)
    }

    @Override
    public boolean hasAutomaticTransition() {
        return false;
    }
}
