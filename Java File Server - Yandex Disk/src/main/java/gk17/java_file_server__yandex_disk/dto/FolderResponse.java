package gk17.java_file_server__yandex_disk.dto;

// Ответ при создании папки
public record FolderResponse(
        String message,
        Long folderId,
        String path,
        String type
) {
}
