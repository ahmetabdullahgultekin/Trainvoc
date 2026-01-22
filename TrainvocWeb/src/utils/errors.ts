import { AxiosError } from 'axios';

/**
 * Standard error response from the API.
 */
export interface ApiError {
    code: string;
    message: string;
    details?: Record<string, string>;
}

/**
 * Application error codes for consistent error handling.
 */
export const ErrorCodes = {
    // Network errors
    NETWORK_ERROR: 'NETWORK_ERROR',
    TIMEOUT: 'TIMEOUT',

    // Room errors
    ROOM_NOT_FOUND: 'ROOM_NOT_FOUND',
    ROOM_FULL: 'ROOM_FULL',
    ROOM_ALREADY_STARTED: 'ROOM_ALREADY_STARTED',
    INVALID_PASSWORD: 'INVALID_PASSWORD',

    // Player errors
    PLAYER_NOT_FOUND: 'PLAYER_NOT_FOUND',
    PLAYER_ALREADY_IN_ROOM: 'PLAYER_ALREADY_IN_ROOM',

    // Game errors
    GAME_NOT_STARTED: 'GAME_NOT_STARTED',
    GAME_ALREADY_FINISHED: 'GAME_ALREADY_FINISHED',
    INVALID_ANSWER: 'INVALID_ANSWER',

    // Generic errors
    UNKNOWN_ERROR: 'UNKNOWN_ERROR',
    VALIDATION_ERROR: 'VALIDATION_ERROR',
} as const;

export type ErrorCode = typeof ErrorCodes[keyof typeof ErrorCodes];

/**
 * Custom application error class.
 */
export class AppError extends Error {
    public readonly code: ErrorCode;
    public readonly details?: Record<string, string>;
    public readonly originalError?: Error;

    constructor(code: ErrorCode, message: string, details?: Record<string, string>, originalError?: Error) {
        super(message);
        this.name = 'AppError';
        this.code = code;
        this.details = details;
        this.originalError = originalError;
    }
}

/**
 * Extracts a user-friendly error message from various error types.
 */
export function getErrorMessage(error: unknown): string {
    if (error instanceof AppError) {
        return error.message;
    }

    if (error instanceof AxiosError) {
        // Handle API errors
        if (error.response?.data?.message) {
            return error.response.data.message;
        }

        // Handle network errors
        if (error.code === 'ECONNABORTED') {
            return 'Request timed out. Please try again.';
        }

        if (!error.response) {
            return 'Network error. Please check your connection.';
        }

        // Handle HTTP status codes
        switch (error.response.status) {
            case 400:
                return 'Invalid request. Please check your input.';
            case 401:
                return 'Unauthorized. Please log in again.';
            case 403:
                return 'Access denied.';
            case 404:
                return 'Resource not found.';
            case 409:
                return 'Conflict. The resource may have been modified.';
            case 429:
                return 'Too many requests. Please try again later.';
            case 500:
                return 'Server error. Please try again later.';
            default:
                return `Request failed with status ${error.response.status}`;
        }
    }

    if (error instanceof Error) {
        return error.message;
    }

    return 'An unexpected error occurred.';
}

/**
 * Extracts error code from API response or returns generic code.
 */
export function getErrorCode(error: unknown): ErrorCode {
    if (error instanceof AppError) {
        return error.code;
    }

    if (error instanceof AxiosError) {
        if (error.response?.data?.code) {
            const code = error.response.data.code;
            if (Object.values(ErrorCodes).includes(code)) {
                return code;
            }
        }

        if (!error.response) {
            return ErrorCodes.NETWORK_ERROR;
        }

        if (error.code === 'ECONNABORTED') {
            return ErrorCodes.TIMEOUT;
        }
    }

    return ErrorCodes.UNKNOWN_ERROR;
}

/**
 * Creates an AppError from an API response or unknown error.
 */
export function parseError(error: unknown): AppError {
    const code = getErrorCode(error);
    const message = getErrorMessage(error);
    const originalError = error instanceof Error ? error : undefined;

    let details: Record<string, string> | undefined;
    if (error instanceof AxiosError && error.response?.data?.details) {
        details = error.response.data.details;
    }

    return new AppError(code, message, details, originalError);
}

/**
 * Type guard to check if an error is an AppError with a specific code.
 */
export function isErrorCode(error: unknown, code: ErrorCode): boolean {
    if (error instanceof AppError) {
        return error.code === code;
    }
    return getErrorCode(error) === code;
}

/**
 * Logs error to console with context for debugging.
 */
export function logError(context: string, error: unknown): void {
    const appError = error instanceof AppError ? error : parseError(error);

    console.error(`[${context}] ${appError.code}: ${appError.message}`, {
        details: appError.details,
        originalError: appError.originalError,
    });
}
