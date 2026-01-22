/**
 * Utility exports.
 * Common utility functions and helpers.
 */

export {
    AppError,
    ErrorCodes,
    getErrorMessage,
    getErrorCode,
    parseError,
    isErrorCode,
    logError,
} from './errors';

export type { ApiError, ErrorCode } from './errors';

export {
    isFullscreen,
    enterFullscreen,
    exitFullscreen,
    toggleFullscreen,
    onFullscreenChange,
} from './fullscreen';
