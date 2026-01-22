/**
 * Avatar list for player profiles.
 * Centralized to ensure consistency across components.
 */
export const AVATAR_LIST = [
    '🦊', '🐱', '🐶', '🐵', '🐸', '🐼', '🐧', '🐯', '🦁', '🐮',
    '🐨', '🐰', '🐻', '🐷', '🐔', '🦄', '🐙', '🐢', '🐳', '🐝'
] as const;

export type Avatar = typeof AVATAR_LIST[number];

/**
 * Gets an avatar by index, wrapping around if index exceeds list length.
 */
export function getAvatarByIndex(index: number): Avatar {
    return AVATAR_LIST[index % AVATAR_LIST.length];
}

/**
 * Gets a random avatar from the list.
 */
export function getRandomAvatar(): Avatar {
    return AVATAR_LIST[Math.floor(Math.random() * AVATAR_LIST.length)];
}
