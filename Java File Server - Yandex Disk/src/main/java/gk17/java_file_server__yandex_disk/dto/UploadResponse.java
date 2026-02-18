package gk17.java_file_server__yandex_disk.dto;

// Ответ при загрузке файла
public record UploadResponse(
        String message,
        Long fileId,
        String path,
        String name,
        Long size,
        String type
) {
}
