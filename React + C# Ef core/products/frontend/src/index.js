import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {Main} from "./components/view/Main";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Main />
  </React.StrictMode>
);

// Работа вкладок
// Главные вкладки: верхние
// Для каждой вкладки есть свои: слева
//
// Цепочка:n
// В MainContent указываем верхнюю вкладку
// внутри её указываем левые вкладки и суем LeftBar (см Specification.js)
// также для верхней вкладки создать Content компонент, в которой уже указывается что отображать
// в верхней вкладки указываем через id наши собственные значение
// в Content для этих значених указываем название компонента
// id -> myValue -> nameComponent


// Запросы
// 
//
//
//
//