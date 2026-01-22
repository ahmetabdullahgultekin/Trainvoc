import React, {Component} from 'react';
import type {ErrorInfo, ReactNode} from 'react';
import {Box, Button, Container, Typography} from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

interface ErrorBoundaryProps {
    children: ReactNode;
    fallback?: ReactNode;
    onError?: (error: Error, errorInfo: ErrorInfo) => void;
}

interface ErrorBoundaryState {
    hasError: boolean;
    error: Error | null;
}

/**
 * Error Boundary component for catching and handling React errors gracefully.
 * Prevents the entire app from crashing when a component throws an error.
 *
 * Usage:
 * <ErrorBoundary>
 *   <ComponentThatMightError />
 * </ErrorBoundary>
 *
 * With custom fallback:
 * <ErrorBoundary fallback={<CustomErrorUI />}>
 *   <ComponentThatMightError />
 * </ErrorBoundary>
 */
class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
    constructor(props: ErrorBoundaryProps) {
        super(props);
        this.state = {hasError: false, error: null};
    }

    static getDerivedStateFromError(error: Error): ErrorBoundaryState {
        return {hasError: true, error};
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
        // Log error to console in development
        if (import.meta.env.MODE === 'development') {
            console.error('ErrorBoundary caught an error:', error);
            console.error('Component stack:', errorInfo.componentStack);
        }

        // Call optional error handler
        this.props.onError?.(error, errorInfo);
    }

    handleRetry = (): void => {
        this.setState({hasError: false, error: null});
    };

    handleGoHome = (): void => {
        window.location.href = '/';
    };

    render(): ReactNode {
        if (this.state.hasError) {
            // Custom fallback provided
            if (this.props.fallback) {
                return this.props.fallback;
            }

            // Default error UI
            return (
                <Container maxWidth="sm">
                    <Box
                        sx={{
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            justifyContent: 'center',
                            minHeight: '60vh',
                            textAlign: 'center',
                            gap: 2,
                        }}
                    >
                        <ErrorOutlineIcon sx={{fontSize: 80, color: 'error.main'}} />
                        <Typography variant="h4" component="h1" gutterBottom>
                            Bir Şeyler Yanlış Gitti
                        </Typography>
                        <Typography variant="body1" color="text.secondary" sx={{mb: 2}}>
                            Beklenmeyen bir hata oluştu. Lütfen sayfayı yenileyinizi veya ana sayfaya dönmeyi deneyin.
                        </Typography>
                        {import.meta.env.MODE === 'development' && this.state.error && (
                            <Box
                                sx={{
                                    p: 2,
                                    bgcolor: 'grey.100',
                                    borderRadius: 1,
                                    width: '100%',
                                    overflow: 'auto',
                                    textAlign: 'left',
                                }}
                            >
                                <Typography variant="caption" component="pre" sx={{fontFamily: 'monospace'}}>
                                    {this.state.error.message}
                                </Typography>
                            </Box>
                        )}
                        <Box sx={{display: 'flex', gap: 2}}>
                            <Button variant="contained" onClick={this.handleRetry}>
                                Tekrar Dene
                            </Button>
                            <Button variant="outlined" onClick={this.handleGoHome}>
                                Ana Sayfaya Dön
                            </Button>
                        </Box>
                    </Box>
                </Container>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;
