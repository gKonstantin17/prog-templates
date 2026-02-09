import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import FileManager from './components/FileManager';
import Profile from './components/Profile';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Проверяем, есть ли токен в localStorage
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');

    if (token && userData) {
      setIsAuthenticated(true);
      setUser(JSON.parse(userData));
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData, token) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setIsAuthenticated(true);
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
  };

  const updateUser = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem('user', JSON.stringify(updatedUser));
  };

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  return (
      <Router>
        <div className="App">
          {isAuthenticated && <Navbar user={user} onLogout={handleLogout} />}
          <Routes>
            <Route
                path="/login"
                element={
                  isAuthenticated ?
                      <Navigate to="/files" /> :
                      <Login onLogin={handleLogin} />
                }
            />
            <Route
                path="/register"
                element={
                  isAuthenticated ?
                      <Navigate to="/files" /> :
                      <Login onLogin={handleLogin} isRegister={true} />
                }
            />
            <Route
                path="/files"
                element={
                  isAuthenticated ?
                      <FileManager /> :
                      <Navigate to="/login" />
                }
            />
            <Route
                path="/profile"
                element={
                  isAuthenticated ?
                      <Profile user={user} updateUser={updateUser} /> :
                      <Navigate to="/login" />
                }
            />
            <Route
                path="/"
                element={
                  isAuthenticated ?
                      <Navigate to="/files" /> :
                      <Navigate to="/login" />
                }
            />
          </Routes>
        </div>
      </Router>
  );
}

export default App;