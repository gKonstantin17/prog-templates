package gk17.java_file_server__yandex_disk.dto;

// Запрос на удаление
public record DeleteRequest(
        String path,
        Boolean permanently,
        Boolean forceAsync
) {
    public DeleteRequest {
        permanently = permanently != null ? permanently : false;
        forceAsync = forceAsync != null ? forceAsync : false;
    }
}
