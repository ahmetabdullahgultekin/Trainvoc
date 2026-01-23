/**
 * Custom hooks exports.
 * Provides reusable stateful logic for components.
 */

export { useRooms } from './useRooms';
export { useGameState, GameStep } from './useGameState';
export type { GameStepType } from './useGameState';
export { useLobby } from './useLobby';
export { useWebSocket } from './useWebSocket';
export type { ConnectionState, Player, PlayerRanking, Question, RoomSettings, GameEventHandlers } from './useWebSocket';

// Legacy polling hook - deprecated in favor of WebSocket
export { usePolling } from './usePolling';
