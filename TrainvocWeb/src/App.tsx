import React, {lazy, Suspense} from 'react';
import {Route, Routes} from 'react-router-dom';
import Navbar from './components/shared/Navbar';
import Footer from './components/shared/Footer';
import ScrollToTop from './components/shared/ScrollToTop.tsx';
import ErrorBoundary from './components/shared/ErrorBoundary';

const HomePage = lazy(() => import('./pages/HomePage'));
const AboutPage = lazy(() => import('./pages/AboutPage'));
const ContactPage = lazy(() => import('./pages/ContactPage'));
const MobileAppPage = lazy(() => import('./pages/MobileAppPage'));
const PlayLayout = lazy(() => import('./pages/PlayLayout'));

function App() {
    return (
        <ErrorBoundary>
            <ScrollToTop/>
            <Suspense fallback={<div>Loading...</div>}>
                <Routes>
                    <Route path="/" element={<><Navbar/><HomePage/><Footer/></>}/>
                    <Route path="/about" element={<><Navbar/><AboutPage/><Footer/></>}/>
                    <Route path="/contact" element={<><Navbar/><ContactPage/><Footer/></>}/>
                    <Route path="/mobile" element={<><Navbar/><MobileAppPage/><Footer/></>}/>
                    <Route path="/play/*" element={<PlayLayout/>}/>
                </Routes>
            </Suspense>
        </ErrorBoundary>
    );
}

export default App;
