package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;

/**
 * Interface for game state handlers following the State pattern.
 * Each implementation handles behavior for a specific game state.
 */
public interface GameStateHandler {

    /**
     * Returns the game state this handler manages.
     */
    GameState getState();

    /**
     * Calculates remaining time for this state.
     *
     * @param room The game room
     * @param elapsedSeconds Seconds elapsed since state started
     * @return Remaining time in seconds, or 0 if no time limit
     */
    int calculateRemainingTime(GameRoom room, long elapsedSeconds);

    /**
     * Gets the duration for this state in seconds.
     *
     * @param room The game room (some states use room settings)
     * @return Duration in seconds, or 0 for no time limit
     */
    int getStateDuration(GameRoom room);

    /**
     * Determines the next state when this state's time expires.
     *
     * @return The next GameState, or null if no automatic transition
     */
    GameState getNextState();

    /**
     * Checks if this state allows automatic transition when time expires.
     */
    boolean hasAutomaticTransition();
}
