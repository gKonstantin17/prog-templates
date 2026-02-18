package gk17.java_file_server__yandex_disk.dto;

public record ResourceInfo(
        String name,
        String path,
        String type, // "file" или "dir"
        Long size,
        String md5,
        String mimeType,
        String created,
        String modified
) {}
