package gk17.java_file_server__yandex_disk.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "filedata", schema = "public", catalog = "filesdb")
@Data
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "upload_date")
    private Timestamp uploadDate;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Long size;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "parent_path")
    private String parentPath; // путь родительской папки

    @Column(name = "is_deleted")
    private Boolean isDeleted = false; // мягкое удаление
}
