package gk17.java_file_server__yandex_disk.dto;

import java.util.List;

public record FileNode(
        Long id,
        String name,
        String path,
        String type,
        Long size,
        List<FileNode> children
) {
}
