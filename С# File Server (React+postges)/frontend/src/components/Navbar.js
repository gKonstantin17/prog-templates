import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar({ user, onLogout }) {
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <span>Файловый менеджер</span>
            </div>
            <div className="navbar-menu">
                <Link to="/files" className="nav-link">Файлы</Link>
                <Link to="/profile" className="nav-link">Профиль</Link>
                <div className="user-info">
                    <span className="user-name">{user?.name || user?.login}</span>
                    <button onClick={handleLogout} className="logout-btn">Выйти</button>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;