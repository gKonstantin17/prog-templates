package gk17.java_file_server__yandex_disk.dto;

import gk17.java_file_server__yandex_disk.entity.FileData;

// Информация о файле для клиента
public record FileInfoResponse(
        Long id,
        String name,
        String path,
        String type,
        Long size,
        String uploadDate,
        String serviceName,
        String parentPath
) {
    public static FileInfoResponse fromEntity(FileData entity) {
        return new FileInfoResponse(
                entity.getId(),
                entity.getName(),
                entity.getPath(),
                entity.getType(),
                entity.getSize(),
                entity.getUploadDate() != null ? entity.getUploadDate().toString() : null,
                entity.getServiceName(),
                entity.getParentPath()
        );
    }
}
