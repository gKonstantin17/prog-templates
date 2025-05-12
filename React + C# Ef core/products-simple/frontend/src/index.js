import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from "./app/App";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    {/*  App.js является корневым, которое использует все компоненты
    внутри App.js также есть Общие функции и константы проекта */}
    <App />
  </React.StrictMode>
);


