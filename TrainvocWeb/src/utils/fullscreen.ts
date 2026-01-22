/**
 * Fullscreen utilities for cross-browser fullscreen API support.
 */

/**
 * Checks if the document is currently in fullscreen mode.
 */
export function isFullscreen(): boolean {
    return !!document.fullscreenElement;
}

/**
 * Enters fullscreen mode for the given element (defaults to document root).
 */
export async function enterFullscreen(element: HTMLElement = document.documentElement): Promise<void> {
    try {
        if (element.requestFullscreen) {
            await element.requestFullscreen();
        } else if ((element as any).webkitRequestFullscreen) {
            (element as any).webkitRequestFullscreen();
        } else if ((element as any).msRequestFullscreen) {
            (element as any).msRequestFullscreen();
        }
    } catch (error) {
        console.warn('Failed to enter fullscreen:', error);
    }
}

/**
 * Exits fullscreen mode.
 */
export async function exitFullscreen(): Promise<void> {
    try {
        if (document.exitFullscreen) {
            await document.exitFullscreen();
        } else if ((document as any).webkitExitFullscreen) {
            (document as any).webkitExitFullscreen();
        } else if ((document as any).msExitFullscreen) {
            (document as any).msExitFullscreen();
        }
    } catch (error) {
        // Ignore errors when exiting fullscreen (e.g., not in fullscreen)
    }
}

/**
 * Toggles fullscreen mode.
 */
export async function toggleFullscreen(element?: HTMLElement): Promise<void> {
    if (isFullscreen()) {
        await exitFullscreen();
    } else {
        await enterFullscreen(element);
    }
}

/**
 * Adds a fullscreen change event listener.
 * Returns a cleanup function to remove the listener.
 */
export function onFullscreenChange(callback: (isFullscreen: boolean) => void): () => void {
    const handler = () => callback(isFullscreen());
    document.addEventListener('fullscreenchange', handler);
    document.addEventListener('webkitfullscreenchange', handler);
    document.addEventListener('msfullscreenchange', handler);

    return () => {
        document.removeEventListener('fullscreenchange', handler);
        document.removeEventListener('webkitfullscreenchange', handler);
        document.removeEventListener('msfullscreenchange', handler);
    };
}
