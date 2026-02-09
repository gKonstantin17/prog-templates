import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Login.css';

const API_URL = 'https://localhost:7093';

function Login({ onLogin, isRegister = false }) {
    const [formData, setFormData] = useState({
        name: '',
        login: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const endpoint = isRegister ? '/register' : '/login';
            const response = await axios.post(`${API_URL}${endpoint}`, {
                name: formData.name || undefined,
                login: formData.login,
                password: formData.password
            });

            if (response.data) {
                // В вашем API нет токена, используем данные пользователя
                onLogin(response.data, 'dummy-token');
                navigate('/files');
            }
        } catch (err) {
            setError(isRegister ? 'Ошибка регистрации' : 'Неверный логин или пароль');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <h2>{isRegister ? 'Регистрация' : 'Вход'}</h2>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit}>
                    {isRegister && (
                        <div className="form-group">
                            <label htmlFor="name">Имя:</label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                placeholder="Введите ваше имя"
                            />
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="login">Логин:</label>
                        <input
                            type="text"
                            id="login"
                            name="login"
                            value={formData.login}
                            onChange={handleChange}
                            placeholder="Введите логин"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Пароль:</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Введите пароль"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Загрузка...' : (isRegister ? 'Зарегистрироваться' : 'Войти')}
                    </button>
                </form>

                <div className="auth-switch">
                    {isRegister ? (
                        <p>
                            Уже есть аккаунт? <Link to="/login">Войдите</Link>
                        </p>
                    ) : (
                        <p>
                            Нет аккаунта? <Link to="/register">Зарегистрируйтесь</Link>
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Login;