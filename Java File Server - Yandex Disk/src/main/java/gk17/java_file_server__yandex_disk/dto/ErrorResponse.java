package gk17.java_file_server__yandex_disk.dto;

// Ответ с ошибкой
public record ErrorResponse(
        String error,
        String timestamp
) {
}
