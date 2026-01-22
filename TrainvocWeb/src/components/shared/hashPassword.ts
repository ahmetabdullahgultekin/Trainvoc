/**
 * Simple SHA-256 hash function for room passwords.
 *
 * SECURITY NOTE: This is a basic hash for room access control, not user authentication.
 * For production:
 * - Always use HTTPS to protect data in transit
 * - Consider moving password hashing to the server using bcrypt/argon2
 * - Avoid sending passwords/hashes in URL parameters
 *
 * @param password - The plain text password to hash
 * @returns Promise resolving to hex-encoded SHA-256 hash
 */
export async function hashPassword(password: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await window.crypto.subtle.digest('SHA-256', data);
    return Array.from(new Uint8Array(hashBuffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
}
