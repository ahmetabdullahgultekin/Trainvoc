package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

/**
 * Handles the RANKING state - showing final rankings before game ends.
 */
@Component
public class RankingStateHandler implements GameStateHandler {

    @Override
    public GameState getState() {
        return GameState.RANKING;
    }

    @Override
    public int calculateRemainingTime(GameRoom room, long elapsedSeconds) {
        return Math.max(0, GameConstants.RANKING_SECONDS - (int) elapsedSeconds);
    }

    @Override
    public int getStateDuration(GameRoom room) {
        return GameConstants.RANKING_SECONDS;
    }

    @Override
    public GameState getNextState() {
        return GameState.FINAL;
    }

    @Override
    public boolean hasAutomaticTransition() {
        return true;
    }
}
