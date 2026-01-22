package com.rollingcatsoftware.trainvocmultiplayerapplication.pattern.state;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * State machine for managing game state transitions.
 * Uses the State pattern to delegate behavior to specific handlers.
 */
@Component
public class GameStateMachine {

    private final Map<GameState, GameStateHandler> handlers = new EnumMap<>(GameState.class);

    public GameStateMachine(List<GameStateHandler> stateHandlers) {
        for (GameStateHandler handler : stateHandlers) {
            handlers.put(handler.getState(), handler);
        }
    }

    /**
     * Gets the handler for a specific state.
     */
    public GameStateHandler getHandler(GameState state) {
        return handlers.get(state);
    }

    /**
     * Calculates and potentially updates the current state based on elapsed time.
     *
     * @param room The game room
     * @return Result containing current state and remaining time
     */
    public StateTransitionResult calculateState(GameRoom room) {
        GameState currentState = room.getCurrentState();
        GameStateHandler handler = getHandler(currentState);

        if (handler == null) {
            return new StateTransitionResult(currentState, 0, false);
        }

        long elapsedSeconds = getElapsedSeconds(room);
        int remainingTime = handler.calculateRemainingTime(room, elapsedSeconds);

        // Check if automatic transition should occur
        if (handler.hasAutomaticTransition() && remainingTime <= 0) {
            GameState nextState = handler.getNextState();
            if (nextState != null) {
                return new StateTransitionResult(nextState, getInitialTime(nextState, room), true);
            }
        }

        return new StateTransitionResult(currentState, remainingTime, false);
    }

    /**
     * Gets the initial remaining time for a state.
     */
    public int getInitialTime(GameState state, GameRoom room) {
        GameStateHandler handler = getHandler(state);
        return handler != null ? handler.getStateDuration(room) : 0;
    }

    /**
     * Checks if a state has automatic transition.
     */
    public boolean hasAutomaticTransition(GameState state) {
        GameStateHandler handler = getHandler(state);
        return handler != null && handler.hasAutomaticTransition();
    }

    /**
     * Gets the next state after automatic transition.
     */
    public GameState getNextState(GameState currentState) {
        GameStateHandler handler = getHandler(currentState);
        return handler != null ? handler.getNextState() : null;
    }

    private long getElapsedSeconds(GameRoom room) {
        LocalDateTime stateStart = room.getStateStartTime();
        if (stateStart == null) {
            return 0;
        }
        return Duration.between(stateStart, LocalDateTime.now()).getSeconds();
    }

    /**
     * Result of state calculation.
     */
    public record StateTransitionResult(
            GameState state,
            int remainingTime,
            boolean transitioned
    ) {}
}
