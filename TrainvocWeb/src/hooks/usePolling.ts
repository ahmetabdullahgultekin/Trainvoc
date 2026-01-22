import { useState, useEffect, useCallback, useRef } from 'react';

interface UsePollingOptions<T> {
    fetchFn: () => Promise<T>;
    interval: number;
    enabled?: boolean;
    onSuccess?: (data: T) => void;
    onError?: (error: Error) => void;
}

interface UsePollingResult<T> {
    data: T | null;
    loading: boolean;
    error: Error | null;
    refresh: () => Promise<void>;
    stop: () => void;
    start: () => void;
}

/**
 * Generic polling hook for periodic data fetching.
 * Handles cleanup, error states, and manual refresh.
 */
export function usePolling<T>(options: UsePollingOptions<T>): UsePollingResult<T> {
    const {
        fetchFn,
        interval,
        enabled = true,
        onSuccess,
        onError,
    } = options;

    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);
    const [isPolling, setIsPolling] = useState(enabled);
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
    const mountedRef = useRef(true);

    const fetchData = useCallback(async () => {
        try {
            const result = await fetchFn();
            if (!mountedRef.current) return;

            setData(result);
            setError(null);
            onSuccess?.(result);
        } catch (err) {
            if (!mountedRef.current) return;

            const error = err instanceof Error ? err : new Error('Unknown error');
            setError(error);
            onError?.(error);
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    }, [fetchFn, onSuccess, onError]);

    const stop = useCallback(() => {
        setIsPolling(false);
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
        }
    }, []);

    const start = useCallback(() => {
        setIsPolling(true);
    }, []);

    const refresh = useCallback(async () => {
        setLoading(true);
        await fetchData();
    }, [fetchData]);

    useEffect(() => {
        mountedRef.current = true;

        if (!isPolling) {
            return;
        }

        // Initial fetch
        fetchData();

        // Set up interval
        intervalRef.current = setInterval(fetchData, interval);

        return () => {
            mountedRef.current = false;
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [fetchData, interval, isPolling]);

    return {
        data,
        loading,
        error,
        refresh,
        stop,
        start,
    };
}

export default usePolling;
