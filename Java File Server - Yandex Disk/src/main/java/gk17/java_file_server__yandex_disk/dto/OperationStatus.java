package gk17.java_file_server__yandex_disk.dto;

// Статус операции
public record OperationStatus(
        String href,
        String method,
        boolean templated,
        String status
) {
}
