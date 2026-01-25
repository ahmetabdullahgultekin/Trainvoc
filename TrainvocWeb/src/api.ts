import axios from 'axios';

// Get API URL from environment variable
const getBaseURL = (): string => {
    const envUrl = import.meta.env.VITE_API_URL;

    if (envUrl) {
        return envUrl;
    }

    // In development, fall back to localhost
    if (import.meta.env.DEV) {
        console.warn('API: VITE_API_URL not set, using localhost for development');
        return 'http://localhost:8080/';
    }

    // In production, derive from current location (assumes API is on same domain with /api path)
    // Or throw error if API URL is required
    console.warn('API: VITE_API_URL not configured for production');
    return `${window.location.origin}/`;
};

const api = axios.create({
    baseURL: getBaseURL(),
    withCredentials: true
});

export default api;
