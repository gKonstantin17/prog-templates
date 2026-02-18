package gk17.java_file_server__yandex_disk.repository;

import gk17.java_file_server__yandex_disk.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileData, Long> {
    Optional<FileData> findById(Long id);

    Optional<FileData> findByPath(String path);

    // Исправляем запрос с boolean
    List<FileData> findByParentPathAndIsDeletedFalse(String parentPath);

    // Используем native query для сложных запросов
    @Query(value = "SELECT DISTINCT f.parent_path FROM filedata f WHERE f.parent_path LIKE ?1% AND f.is_deleted = false", nativeQuery = true)
    List<String> findDistinctParentPaths(String rootPath);

    @Query(value = "SELECT f.* FROM filedata f WHERE f.path LIKE CONCAT(:path, '%') AND f.is_deleted = false", nativeQuery = true)
    List<FileData> findAllByPathStartingWith(@Param("path") String path);

    void deleteByPath(String path);

    // Добавляем метод для физического удаления
    void deleteByPathAndIsDeletedTrue(String path);
}
