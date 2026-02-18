package gk17.java_file_server__yandex_disk.dto;

public record MoveRequest(
        String from,
        String path,
        Boolean overwrite,
        Boolean forceAsync
) {
    public MoveRequest {
        overwrite = overwrite != null ? overwrite : false;
        forceAsync = forceAsync != null ? forceAsync : false;
    }
}
