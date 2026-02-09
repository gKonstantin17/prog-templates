import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './FileManager.css';

const API_URL = 'https://localhost:7093';

function FileManager() {
    const [files, setFiles] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchFiles();
    }, []);

    const fetchFiles = async () => {
        try {
            setLoading(true);
            const response = await axios.get(`${API_URL}/files`);
            setFiles(response.data);
        } catch (err) {
            setError('Ошибка при загрузке файлов');
        } finally {
            setLoading(false);
        }
    };

    const handleFileSelect = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleUpload = async (e) => {
        e.preventDefault();
        if (!selectedFile) {
            setError('Выберите файл для загрузки');
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);

        try {
            setUploading(true);
            setError('');
            await axios.post(`${API_URL}/upload-file`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            setSelectedFile(null);
            document.getElementById('file-input').value = '';
            fetchFiles();
        } catch (err) {
            setError('Ошибка при загрузке файла');
        } finally {
            setUploading(false);
        }
    };

    const handleDownload = async (fileId, fileName) => {
        try {
            const response = await axios.get(`${API_URL}/download/${fileId}`, {
                responseType: 'blob'
            });

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (err) {
            setError('Ошибка при скачивании файла');
        }
    };

    const handleDelete = async (fileId) => {
        if (!window.confirm('Вы уверены, что хотите удалить этот файл?')) {
            return;
        }

        try {
            await axios.delete(`${API_URL}/delete/${fileId}`);
            fetchFiles();
        } catch (err) {
            setError('Ошибка при удалении файла');
        }
    };

    const formatFileSize = (bytes) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString('ru-RU');
    };

    return (
        <div className="file-manager">
            <div className="upload-section">
                <h2>Загрузка файла</h2>
                <form onSubmit={handleUpload} className="upload-form">
                    <div className="file-input-container">
                        <input
                            type="file"
                            id="file-input"
                            onChange={handleFileSelect}
                            className="file-input"
                        />
                        <label htmlFor="file-input" className="file-label">
                            {selectedFile ? selectedFile.name : 'Выберите файл'}
                        </label>
                    </div>
                    <button
                        type="submit"
                        className="upload-btn"
                        disabled={!selectedFile || uploading}
                    >
                        {uploading ? 'Загрузка...' : 'Загрузить'}
                    </button>
                </form>
            </div>

            {error && <div className="error-message">{error}</div>}

            <div className="files-section">
                <h2>Список файлов</h2>
                {loading ? (
                    <div className="loading">Загрузка файлов...</div>
                ) : files.length === 0 ? (
                    <div className="no-files">Нет загруженных файлов</div>
                ) : (
                    <div className="files-grid">
                        {files.map((file) => (
                            <div key={file.id} className="file-card">
                                <div className="file-info">
                                    <div className="file-name">{file.name}</div>
                                    <div className="file-details">
                                        <span>Размер: {formatFileSize(file.size || 0)}</span>
                                        <span>Дата: {formatDate(file.uploadDate)}</span>
                                    </div>
                                </div>
                                <div className="file-actions">
                                    <button
                                        onClick={() => handleDownload(file.id, file.name)}
                                        className="action-btn download-btn"
                                    >
                                        Скачать
                                    </button>
                                    <button
                                        onClick={() => handleDelete(file.id)}
                                        className="action-btn delete-btn"
                                    >
                                        Удалить
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default FileManager;