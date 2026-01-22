import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/api': 'http://localhost:8080',
        },
    },
    build: {
        rollupOptions: {
            output: {
                manualChunks(id) {
                    if (id.includes('node_modules')) {
                        return 'vendor';
                    }
                    if (id.includes('src/pages/')) {
                        return 'pages';
                    }
                },
            },
        },
        chunkSizeWarningLimit: 800, // Uyarı sınırını artırdık
    },
});
