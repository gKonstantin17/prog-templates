import React, { useState } from 'react';
import axios from 'axios';
import './Profile.css';

const API_URL = 'https://localhost:7093';

function Profile({ user, updateUser }) {
    const [selectedPhoto, setSelectedPhoto] = useState(null);
    const [photoPreview, setPhotoPreview] = useState(user?.photo || '');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handlePhotoSelect = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Проверка типа файла
        if (!file.type.startsWith('image/')) {
            setError('Выберите файл изображения');
            return;
        }

        // Проверка размера (максимум 5MB)
        if (file.size > 5 * 1024 * 1024) {
            setError('Размер файла не должен превышать 5MB');
            return;
        }

        setSelectedPhoto(file);
        setError('');
        setSuccess('');

        // Превью изображения
        const reader = new FileReader();
        reader.onloadend = () => {
            setPhotoPreview(reader.result);
        };
        reader.readAsDataURL(file);
    };

    const handleUploadPhoto = async (e) => {
        e.preventDefault();
        if (!selectedPhoto || !user?.id) {
            setError('Выберите фотографию');
            return;
        }

        // ОЧЕНЬ ВАЖНО: правильное создание FormData
        const formData = new FormData();
        formData.append('file', selectedPhoto);
        formData.append('userId', user.id.toString());

        try {
            setLoading(true);
            setError('');

            console.log('Отправка фото:', {
                userId: user.id,
                fileName: selectedPhoto.name,
                fileSize: selectedPhoto.size
            });

            const response = await axios.post(`${API_URL}/upload-photo`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            console.log('Ответ от сервера:', response.data);

            // Обновляем пользователя с новой фотографией
            const updatedUser = {
                ...user,
                photo: response.data?.path || photoPreview
            };
            updateUser(updatedUser);
            setSelectedPhoto(null);
            setSuccess('Фотография успешно обновлена');

            // Сбрасываем поле выбора файла
            document.getElementById('photo-input').value = '';

        } catch (err) {
            console.error('Ошибка загрузки фото:', err);
            console.error('Ответ ошибки:', err.response?.data);
            setError(err.response?.data?.title || 'Ошибка при загрузке фотографии');
        } finally {
            setLoading(false);
        }
    };

    const handleDeletePhoto = async () => {
        if (!window.confirm('Удалить фотографию?') || !user?.id) {
            return;
        }

        try {
            setLoading(true);
            await axios.delete(`${API_URL}/delete-photo/${user.id}`);

            const updatedUser = { ...user, photo: null };
            updateUser(updatedUser);
            setPhotoPreview('');
            setSelectedPhoto(null);
            setSuccess('Фотография удалена');
        } catch (err) {
            console.error('Ошибка удаления фото:', err);
            setError('Ошибка при удалении фотографии');
        } finally {
            setLoading(false);
        }
    };

    // Исправленный URL для фото
    const getPhotoUrl = () => {
        if (!photoPreview) return '';

        // Если это data URL (превью)
        if (photoPreview.startsWith('data:')) {
            return photoPreview;
        }

        // Если это путь из базы данных
        if (user?.photo) {
            // Убедитесь, что путь правильный
            return `${API_URL}/${user.photo}`.replace('//', '/');
        }

        return '';
    };

    return (
        <div className="profile-container">
            <div className="profile-card">
                <h2>Профиль пользователя</h2>

                <div className="user-info">
                    <div className="info-item">
                        <span className="label">Имя:</span>
                        <span className="value">{user?.name || 'Не указано'}</span>
                    </div>
                    <div className="info-item">
                        <span className="label">Логин:</span>
                        <span className="value">{user?.login}</span>
                    </div>
                    <div className="info-item">
                        <span className="label">ID:</span>
                        <span className="value">{user?.id}</span>
                    </div>
                </div>

                <div className="photo-section">
                    <h3>Фотография профиля</h3>

                    <div className="photo-preview">
                        {getPhotoUrl() ? (
                            <img
                                src={getPhotoUrl()}
                                alt="Profile"
                                className="profile-photo"
                                onError={(e) => {
                                    console.error('Ошибка загрузки изображения:', getPhotoUrl());
                                    e.target.style.display = 'none';
                                    e.target.parentElement.innerHTML = '<div class="no-photo">Ошибка загрузки фото</div>';
                                }}
                            />
                        ) : (
                            <div className="no-photo">Нет фотографии</div>
                        )}
                    </div>

                    <form onSubmit={handleUploadPhoto} className="photo-form">
                        <div className="file-input-container">
                            <input
                                type="file"
                                id="photo-input"
                                onChange={handlePhotoSelect}
                                accept="image/*"
                                className="file-input"
                                disabled={loading}
                            />
                            <label htmlFor="photo-input" className="file-label">
                                {selectedPhoto ? selectedPhoto.name : 'Выберите фотографию'}
                            </label>
                        </div>

                        <div className="photo-actions">
                            <button
                                type="submit"
                                className="action-btn upload-btn"
                                disabled={!selectedPhoto || loading}
                            >
                                {loading ? 'Загрузка...' : 'Загрузить фото'}
                            </button>

                            {user?.photo && (
                                <button
                                    type="button"
                                    onClick={handleDeletePhoto}
                                    className="action-btn delete-btn"
                                    disabled={loading}
                                >
                                    Удалить фото
                                </button>
                            )}
                        </div>
                    </form>

                    {error && <div className="error-message">{error}</div>}
                    {success && <div className="success-message">{success}</div>}
                </div>
            </div>
        </div>
    );
}

export default Profile;