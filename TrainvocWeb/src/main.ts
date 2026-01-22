import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import {BrowserRouter} from 'react-router-dom';
import './style.css';
import './i18n';

ReactDOM.createRoot(document.getElementById('root')!).render(
    React.createElement(React.StrictMode, null,
        React.createElement(BrowserRouter, null,
            React.createElement(App, null)
        )
    )
);
