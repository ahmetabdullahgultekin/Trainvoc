import { describe, it, expect, vi, beforeEach } from 'vitest';
import { GameService } from '../GameService';
import api from '../../api';
import type { GameRoom, Player } from '../../interfaces/game';

// Mock the api module
vi.mock('../../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

describe('GameService', () => {
    const mockRoom: GameRoom = {
        roomCode: 'ABC123',
        players: [{ id: '1', name: 'Test Player', score: 0, avatarId: 1 }],
        currentQuestionIndex: 0,
        started: false,
        hostId: '1',
        questionDuration: 60,
        optionCount: 4,
        level: 'A1',
        totalQuestionCount: 10,
    };

    const mockPlayer: Player = {
        id: 'player-1',
        name: 'Test Player',
        score: 100,
        avatarId: 5,
    };

    const mockGameState = {
        step: 'QUESTION',
        questionIndex: 2,
        timeLeft: 45,
        question: {
            id: '1',
            text: 'What is the meaning of "apple"?',
            options: ['elma', 'armut', 'muz', 'portakal'],
        },
    };

    const mockSettings = {
        questionDuration: 30,
        optionCount: 4,
        level: 'A1',
        totalQuestionCount: 10,
    };

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('createRoom', () => {
        it('should create room with basic settings', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockRoom });

            const room = await GameService.createRoom('TestHost', mockSettings);

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('/api/game/create?hostName=TestHost&hostWantsToJoin=true'),
                mockSettings
            );
            expect(room).toEqual(mockRoom);
        });

        it('should create room with avatar and password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockRoom });

            await GameService.createRoom('TestHost', mockSettings, 5, 'hashedPassword123');

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('avatarId=5'),
                mockSettings
            );
            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('hashedPassword=hashedPassword123'),
                mockSettings
            );
        });
    });

    describe('joinRoom', () => {
        it('should join room successfully', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockPlayer });

            const player = await GameService.joinRoom('ABC123', 'TestPlayer');

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('/api/game/join?roomCode=ABC123&playerName=TestPlayer')
            );
            expect(player).toEqual(mockPlayer);
        });

        it('should join room with avatar', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockPlayer });

            await GameService.joinRoom('ABC123', 'TestPlayer', 3);

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('avatarId=3')
            );
        });

        it('should join room with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockPlayer });

            await GameService.joinRoom('ABC123', 'TestPlayer', undefined, 'hashedPass');

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('hashedPassword=hashedPass')
            );
        });
    });

    describe('getRoom', () => {
        it('should get room by code', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const room = await GameService.getRoom('ABC123');

            expect(api.get).toHaveBeenCalledWith('/api/game/ABC123');
            expect(room).toEqual(mockRoom);
        });

        it('should return null when room not found', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const room = await GameService.getRoom('INVALID');

            expect(room).toBeNull();
        });
    });

    describe('getAllRooms', () => {
        it('should fetch all rooms', async () => {
            const rooms = [mockRoom, { ...mockRoom, roomCode: 'DEF456' }];
            vi.mocked(api.get).mockResolvedValueOnce({ data: rooms });

            const result = await GameService.getAllRooms();

            expect(api.get).toHaveBeenCalledWith('/api/game/rooms');
            expect(result).toHaveLength(2);
        });
    });

    describe('getPlayers', () => {
        it('should fetch players for a room', async () => {
            const players = [mockPlayer, { ...mockPlayer, id: '2', name: 'Player 2' }];
            vi.mocked(api.get).mockResolvedValueOnce({ data: players });

            const result = await GameService.getPlayers('ABC123');

            expect(api.get).toHaveBeenCalledWith('/api/game/players', {
                params: { roomCode: 'ABC123' },
            });
            expect(result).toHaveLength(2);
        });
    });

    describe('startRoom', () => {
        it('should start room without password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await GameService.startRoom('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/start?');
        });

        it('should start room with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await GameService.startRoom('ABC123', 'hashedPass');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/start?hashedPassword=hashedPass'
            );
        });
    });

    describe('disbandRoom', () => {
        it('should disband room without password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await GameService.disbandRoom('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/disband?');
        });

        it('should disband room with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await GameService.disbandRoom('ABC123', 'hashedPass');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/disband?hashedPassword=hashedPass'
            );
        });
    });

    describe('leaveRoom', () => {
        it('should leave room', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await GameService.leaveRoom('ABC123', 'player-1');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/leave',
                null,
                { params: { playerId: 'player-1' } }
            );
        });
    });

    describe('getGameState', () => {
        it('should get game state', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockGameState });

            const state = await GameService.getGameState('ABC123', 'player-1');

            expect(api.get).toHaveBeenCalledWith('/api/game/state', {
                params: { roomCode: 'ABC123', playerId: 'player-1' },
            });
            expect(state).toEqual(mockGameState);
        });

        it('should return null on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Error'));

            const state = await GameService.getGameState('ABC123', 'player-1');

            expect(state).toBeNull();
        });
    });

    describe('nextQuestion', () => {
        it('should advance to next question', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockGameState });

            const state = await GameService.nextQuestion('ABC123');

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('/api/game/next?roomCode=ABC123')
            );
            expect(state).toEqual(mockGameState);
        });

        it('should advance with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: mockGameState });

            await GameService.nextQuestion('ABC123', 'hashedPass');

            expect(api.post).toHaveBeenCalledWith(
                expect.stringContaining('hashedPassword=hashedPass')
            );
        });
    });

    describe('submitAnswer', () => {
        it('should submit answer', async () => {
            const answerRequest = {
                roomCode: 'ABC123',
                playerId: 'player-1',
                answer: 'elma',
                answerTime: 5000,
                isCorrect: true,
                optionPickRate: 0.25,
            };
            const response = { players: [mockPlayer] };
            vi.mocked(api.post).mockResolvedValueOnce({ data: response });

            const result = await GameService.submitAnswer(answerRequest);

            expect(api.post).toHaveBeenCalledWith('/api/game/answer', answerRequest);
            expect(result.players).toEqual([mockPlayer]);
        });
    });
});
