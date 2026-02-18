package gk17.java_file_server__yandex_disk.dto;

// Ответ при успешной операции
public record SuccessResponse(
        String message,
        Object data
) {
}
