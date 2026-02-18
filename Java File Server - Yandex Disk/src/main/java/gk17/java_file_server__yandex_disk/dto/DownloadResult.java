package gk17.java_file_server__yandex_disk.dto;

public record DownloadResult(
        byte[] content,
        String filename,
        String path
) {}