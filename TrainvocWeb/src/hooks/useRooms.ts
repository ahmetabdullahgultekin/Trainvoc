import { useState, useEffect, useCallback } from 'react';
import type { GameRoom } from '../interfaces/game';
import { RoomService } from '../services';

interface UseRoomsOptions {
    autoRefresh?: boolean;
    refreshInterval?: number;
    filterStarted?: boolean;
}

interface UseRoomsResult {
    rooms: GameRoom[];
    loading: boolean;
    error: string | null;
    refresh: () => Promise<void>;
    availableRooms: GameRoom[];
    startedRooms: GameRoom[];
}

/**
 * Hook for fetching and managing room list.
 */
export function useRooms(options: UseRoomsOptions = {}): UseRoomsResult {
    const {
        autoRefresh = true,
        refreshInterval = 5000,
        filterStarted,
    } = options;

    const [rooms, setRooms] = useState<GameRoom[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const refresh = useCallback(async () => {
        try {
            setError(null);
            const fetchedRooms = await RoomService.fetchRooms();
            setRooms(fetchedRooms);
        } catch (err) {
            setError('Failed to fetch rooms');
            console.error('Error fetching rooms:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        refresh();

        if (autoRefresh) {
            const interval = setInterval(refresh, refreshInterval);
            return () => clearInterval(interval);
        }
    }, [refresh, autoRefresh, refreshInterval]);

    const filteredRooms = filterStarted !== undefined
        ? RoomService.filterByStatus(rooms, filterStarted)
        : rooms;

    const availableRooms = RoomService.filterAvailable(rooms);
    const startedRooms = RoomService.filterByStatus(rooms, true);

    return {
        rooms: filteredRooms,
        loading,
        error,
        refresh,
        availableRooms,
        startedRooms,
    };
}

export default useRooms;
