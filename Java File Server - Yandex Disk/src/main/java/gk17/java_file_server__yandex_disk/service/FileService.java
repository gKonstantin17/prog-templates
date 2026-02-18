package gk17.java_file_server__yandex_disk.service;

import gk17.java_file_server__yandex_disk.dto.*;
import gk17.java_file_server__yandex_disk.entity.FileData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class FileService {
    private final DbService dbService;
    private final YandexService yandexService;

    public FileService(DbService dbService, YandexService yandexService) {
        this.dbService = dbService;
        this.yandexService = yandexService;
    }

    // Загрузка файла
    public FileData uploadFile(MultipartFile file, String path) throws IOException {
        // Нормализуем путь
        String normalizedPath = normalizePath(path);
        String fullPath = normalizedPath + file.getOriginalFilename();

        // Загружаем на Яндекс.Диск
        yandexService.uploadFile(file, fullPath);

        // Сохраняем в БД
        String extension = getFileExtension(file.getOriginalFilename());
        return dbService.saveFileInfo(
                file.getOriginalFilename(),
                fullPath,
                file.getSize(),
                "YANDEX_DISK",
                extension
        );
    }

    // Создание папки
    public FileData createFolder(String path) {
        boolean created = yandexService.createDirectory(path);

        if (created) {
            String folderName = path.substring(path.lastIndexOf("/") + 1);
            return dbService.saveFolderInfo(folderName, path);
        }

        throw new RuntimeException("Folder already exists: " + path);
    }

    // Скачивание файла по ID
    public DownloadResult downloadFileById(Long id) throws IOException {
        Optional<FileInfoResponse> fileInfo = dbService.getFileInfo(id);

        if (fileInfo.isEmpty()) {
            throw new RuntimeException("File not found with id: " + id);
        }

        FileInfoResponse fileData = fileInfo.get();
        InputStream fileStream = yandexService.downloadFile(fileData.path());
        byte[] fileBytes = fileStream.readAllBytes();

        return new DownloadResult(
                fileBytes,
                fileData.name(),
                fileData.path()
        );
    }

    // Скачивание файла по пути
    public DownloadResult downloadFileByPath(String path) throws IOException {
        Optional<FileData> fileData = dbService.getFileDataByPath(path);

        if (fileData.isEmpty()) {
            throw new RuntimeException("File not found with path: " + path);
        }

        FileData file = fileData.get();
        InputStream fileStream = yandexService.downloadFile(file.getPath());
        byte[] fileBytes = fileStream.readAllBytes();

        return new DownloadResult(
                fileBytes,
                file.getName(),
                file.getPath()
        );
    }

    // Перемещение файла
    public Object moveFile(String from, String destinationPath, Boolean overwrite, Boolean forceAsync) {
        // Проверяем, что путь назначения корректен
        String normalizedDestination = destinationPath.startsWith("/") ? destinationPath : "/" + destinationPath;

        // Проверяем, что исходный файл существует в БД
        Optional<FileData> sourceFile = dbService.getFileDataByPath(from);
        if (sourceFile.isEmpty()) {
            throw new RuntimeException("Source file not found in database: " + from);
        }

        // Перемещаем на Яндекс.Диске
        Object response = yandexService.moveResource(
                from,
                normalizedDestination,
                overwrite,
                forceAsync
        );

        // Обновляем информацию в БД
        String newName = normalizedDestination.substring(normalizedDestination.lastIndexOf("/") + 1);
        dbService.updateFileInfo(from, normalizedDestination, newName);

        return response;
    }

    // Синхронизация с Яндекс.Диском
    public SyncResult syncWithYandex(String path) {
        ResourceInfo info = yandexService.getResourceInfo(path);

        // Проверяем, есть ли запись в БД
        Optional<FileData> existingFile = dbService.getFileDataByPath(path);
        boolean isNew = existingFile.isEmpty();

        if (isNew) {
            // Если нет - создаем
            if ("dir".equals(info.type())) {
                String folderName = path.substring(path.lastIndexOf("/") + 1);
                dbService.saveFolderInfo(folderName, path);
            } else {
                String extension = getFileExtension(info.name());
                dbService.saveFileInfo(
                        info.name(),
                        path,
                        info.size(),
                        "YANDEX_DISK",
                        extension
                );
            }
        } else {
            // Если есть - обновляем
            FileData fileData = existingFile.get();
            fileData.setName(info.name());
            fileData.setSize(info.size());
            fileData.setType("dir".equals(info.type()) ? "FOLDER" : getFileExtension(info.name()));
            dbService.updateFileInfo(path, path, info.name());
        }

        return new SyncResult(isNew ? "ADDED" : "UPDATED", info);
    }

    // Удаление файла по ID
    public Object deleteFileById(Long id, boolean permanently, boolean forceAsync) {
        Optional<FileInfoResponse> fileInfo = dbService.getFileInfo(id);

        if (fileInfo.isEmpty()) {
            throw new RuntimeException("File not found with id: " + id);
        }

        Object response = yandexService.deleteResource(
                fileInfo.get().path(),
                permanently,
                forceAsync
        );

        dbService.hardDeleteFileInfo(fileInfo.get().path());

        return response;
    }

    // Удаление файла по пути
    public Object deleteFileByPath(String path, boolean permanently, boolean forceAsync) {
        Object response = yandexService.deleteResource(
                path,
                permanently,
                forceAsync
        );

        dbService.hardDeleteFileInfo(path);

        return response;
    }

    // Мягкое удаление по ID
    public void softDeleteById(Long id) {
        Optional<FileInfoResponse> fileInfo = dbService.getFileInfo(id);

        if (fileInfo.isPresent()) {
            dbService.softDeleteFileInfo(fileInfo.get().path());
        } else {
            throw new RuntimeException("File not found with id: " + id);
        }
    }

    // Мягкое удаление по пути
    public void softDeleteByPath(String path) {
        dbService.softDeleteFileInfo(path);
    }

    // Очистка всех помеченных как удаленные
    public void cleanDeleted() {
        List<FileData> deleteMarked = dbService.getDeletedMarked();

        for (FileData marked : deleteMarked) {
            yandexService.deleteResource(marked.getPath(), true, true);
        }

        dbService.hardDeleteAllMarked();
    }

    // Восстановление файлов
    public boolean restore(Set<String> paths) {
        return dbService.restore(paths);
    }

    // Получение информации о ресурсе с Яндекс.Диска
    public ResourceInfo getResourceInfo(String path) {
        return yandexService.getResourceInfo(path);
    }

    // Получение полного дерева
    public FileTree getFullTree() {
        return dbService.getFolderTree("/");
    }

    // Получение дерева от указанного пути
    public FileTree getTreeFromPath(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;

        if (!normalizedPath.equals("/") && !dbService.folderExists(normalizedPath)) {
            throw new RuntimeException("Folder not found: " + path);
        }

        return dbService.getFolderTree(normalizedPath);
    }

    // Вспомогательные методы
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1);
        }
        return "unknown";
    }
}