package gk17.java_file_server__yandex_disk.service;

import gk17.java_file_server__yandex_disk.dto.FileInfoResponse;
import gk17.java_file_server__yandex_disk.dto.FileNode;
import gk17.java_file_server__yandex_disk.dto.FileTree;
import gk17.java_file_server__yandex_disk.entity.FileData;
import gk17.java_file_server__yandex_disk.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DbService {
    private final FileRepository repository;

    public DbService(FileRepository repository) {
        this.repository = repository;
    }

    public FileData saveFileInfo(String originalName, String yandexPath,
                                 Long size, String serviceName, String type) {
        // Нормализуем путь - добавляем ведущий слеш если его нет
        String normalizedPath = normalizePath(yandexPath);

        FileData fileData = new FileData();
        fileData.setName(originalName);
        fileData.setPath(normalizedPath);
        fileData.setUploadDate(new Timestamp(System.currentTimeMillis()));
        fileData.setType(type);
        fileData.setSize(size);
        fileData.setServiceName(serviceName);
        fileData.setParentPath(getParentPath(normalizedPath));
        fileData.setIsDeleted(false);

        return repository.save(fileData);
    }

    public FileData saveFolderInfo(String folderName, String yandexPath) {
        // Нормализуем путь - добавляем ведущий слеш если его нет
        String normalizedPath = normalizePath(yandexPath);

        FileData folderData = new FileData();
        folderData.setName(folderName);
        folderData.setPath(normalizedPath);
        folderData.setUploadDate(new Timestamp(System.currentTimeMillis()));
        folderData.setType("FOLDER");
        folderData.setSize(0L);
        folderData.setServiceName("YANDEX_DISK");
        folderData.setParentPath(getParentPath(normalizedPath));
        folderData.setIsDeleted(false);

        return repository.save(folderData);
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        // Добавляем ведущий слеш если его нет
        if (!path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    private String getParentPath(String fullPath) {
        String normalizedPath = normalizePath(fullPath);
        int lastSlash = normalizedPath.lastIndexOf('/');
        if (lastSlash > 0) {
            return normalizedPath.substring(0, lastSlash);
        }
        return "/";
    }

    public Optional<FileInfoResponse> getFileInfo(Long id) {
        return repository.findById(id)
                .map(fileData -> FileInfoResponse.fromEntity(fileData));
    }

    public Optional<FileData> getFileDataByPath(String path) {
        return repository.findByPath(path);
    }

    public List<FileInfoResponse> getAllFiles() {
        List<FileData> files = repository.findAll();
        List<FileInfoResponse> result = new ArrayList<>();
        for (FileData file : files) {
            if (!file.getIsDeleted()) {
                result.add(FileInfoResponse.fromEntity(file));
            }
        }
        return result;
    }

    // Мягкое удаление
    public void softDeleteFileInfo(String path) {
        Optional<FileData> fileData = repository.findByPath(path);
        fileData.ifPresent(data -> {
            data.setIsDeleted(true);
            repository.save(data);
        });
    }

    // Физическое удаление
    public void hardDeleteFileInfo(String path) {
        repository.deleteByPath(path);
    }

    // Физическое удаление всех помеченных как удаленные
    public void hardDeleteAllMarked() {
        List<FileData> allFiles = repository.findAll();
        for (FileData file : allFiles) {
            if (file.getIsDeleted()) {
                repository.delete(file);
            }
        }
    }

    public List<FileData> getDeletedMarked() {
        return repository.findAll().stream().filter(f -> f.getIsDeleted()).toList();
    }

    public void updateFileInfo(String oldPath, String newPath, String newName) {
        Optional<FileData> fileData = repository.findByPath(oldPath);
        fileData.ifPresent(data -> {
            data.setPath(newPath);
            data.setName(newName);
            data.setParentPath(getParentPath(newPath));
            repository.save(data);
        });
    }

    public boolean restore(Set<String> paths) {
        List<FileData> files = getDeletedMarked();

        files.stream()
                .filter(f -> paths.contains(f.getPath()))
                .forEach(f -> {
                    f.setIsDeleted(false);
                    repository.save(f);
                });

        return true;
    }


    public FileTree getFolderTree(String rootPath) {
        // Нормализуем входной путь
        String normalizedRoot = normalizePath(rootPath);
        log.info("Building tree for normalized path: {}", normalizedRoot);

        // Получаем все файлы
        List<FileData> allFiles = repository.findAll();

        // Фильтруем файлы, которые начинаются с normalizedRoot и не удалены
        List<FileData> filteredFiles = allFiles.stream()
                .filter(f -> !f.getIsDeleted())
                .filter(f -> {
                    String filePath = normalizePath(f.getPath());
                    return filePath.startsWith(normalizedRoot);
                })
                .collect(Collectors.toList());

        log.info("Found {} files for path {}", filteredFiles.size(), normalizedRoot);

        List<FileNode> roots = buildTree(filteredFiles, normalizedRoot);
        return new FileTree(roots);
    }
    public boolean folderExists(String path) {
        String normalizedPath = normalizePath(path);
        return repository.findByPath(normalizedPath)
                .map(fileData -> "FOLDER".equals(fileData.getType()) && !fileData.getIsDeleted())
                .orElse(false);
    }


    private List<FileNode> buildTree(List<FileData> files, String currentPath) {
        List<FileNode> nodes = new ArrayList<>();

        for (FileData file : files) {
            String parentPath = file.getParentPath() != null ? file.getParentPath() : "/";
            if (parentPath.equals(currentPath) && !file.getIsDeleted()) {
                List<FileNode> children = "FOLDER".equals(file.getType())
                        ? buildTree(files, file.getPath())
                        : List.of();

                FileNode node = new FileNode(
                        file.getId(),
                        file.getName(),
                        file.getPath(),
                        file.getType(),
                        file.getSize(),
                        children
                );
                nodes.add(node);
            }
        }
        return nodes;
    }
}
