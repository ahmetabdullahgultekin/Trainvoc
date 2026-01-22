import React from 'react';
import type { ReactElement, ReactNode } from 'react';
import { render } from '@testing-library/react';
import type { RenderOptions } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material';

const theme = createTheme();

interface WrapperProps {
    children: ReactNode;
}

/**
 * Custom render function that wraps components with necessary providers.
 */
function AllProviders({ children }: WrapperProps) {
    return (
        <BrowserRouter>
            <ThemeProvider theme={theme}>
                {children}
            </ThemeProvider>
        </BrowserRouter>
    );
}

/**
 * Custom render function that includes all providers.
 */
function customRender(
    ui: ReactElement,
    options?: Omit<RenderOptions, 'wrapper'>
) {
    return render(ui, { wrapper: AllProviders, ...options });
}

// Re-export everything from testing-library
export * from '@testing-library/react';
export { customRender as render };
