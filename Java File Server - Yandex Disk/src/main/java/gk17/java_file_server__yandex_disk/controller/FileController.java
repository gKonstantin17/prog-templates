package gk17.java_file_server__yandex_disk.controller;

import gk17.java_file_server__yandex_disk.dto.*;
import gk17.java_file_server__yandex_disk.entity.FileData;
import gk17.java_file_server__yandex_disk.service.FileService;
import gk17.java_file_server__yandex_disk.dto.FileTree;
import gk17.java_file_server__yandex_disk.dto.ResourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // загрузка файла в конкретный путь (файл,/путь/)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "/") String path) {

        try {
            FileData savedFile = fileService.uploadFile(file, path);

            return ResponseEntity.ok(new UploadResponse(
                    "File uploaded successfully",
                    savedFile.getId(),
                    savedFile.getPath(),
                    savedFile.getName(),
                    savedFile.getSize(),
                    savedFile.getType()
            ));

        } catch (IOException e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to upload file: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // создание директории (/итоговый/полный/путь)
    @PostMapping("/folder")
    public ResponseEntity<?> createFolder(@RequestParam String path) {
        try {
            FileData savedFolder = fileService.createFolder(path);

            return ResponseEntity.ok(new FolderResponse(
                    "Folder created successfully",
                    savedFolder.getId(),
                    savedFolder.getPath(),
                    "FOLDER"
            ));

        } catch (Exception e) {
            log.error("Error creating folder", e);

            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse(
                                e.getMessage(),
                                LocalDateTime.now().toString()
                        ));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to create folder: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }


    // скачивание файла по id в бд
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        try {
            DownloadResult result = fileService.downloadFileById(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            String encodedFilename = URLEncoder.encode(result.filename(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + result.filename() + "\"; filename*=UTF-8''" + encodedFilename);

            headers.setContentLength(result.content().length);

            return new ResponseEntity<>(result.content(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error downloading file", e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to download file: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }
    // скачивание файла по path
    // начинать со слеша
    @PostMapping("/download")
    public ResponseEntity<?> downloadFileByPath(@RequestBody Path path) {
        try {
            DownloadResult result = fileService.downloadFileByPath(path.path());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            String encodedFilename = URLEncoder.encode(result.filename(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + result.filename() + "\"; filename*=UTF-8''" + encodedFilename);

            headers.setContentLength(result.content().length);

            return new ResponseEntity<>(result.content(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error downloading file", e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to download file: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // перемещение из papka/file.txt в papka2/file.txt
    @PostMapping("/move")
    public ResponseEntity<?> moveFile(@RequestBody MoveRequest request) {
        try {
            Object response = fileService.moveFile(
                    request.from(),
                    request.path(),
                    request.overwrite(),
                    request.forceAsync()
            );

            return ResponseEntity.ok(new SuccessResponse(
                    "Resource moved successfully",
                    response
            ));

        } catch (Exception e) {
            log.error("Error moving resource", e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        e.getMessage(),
                        LocalDateTime.now().toString()
                ));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to move resource: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }
    // если файл есть в на диске, но нет в бд, то указать полный путь файла
    @PostMapping("/sync")
    public ResponseEntity<?> syncWithYandex(@RequestParam String path) {
        try {
            SyncResult result = fileService.syncWithYandex(path);

            return ResponseEntity.ok(new SuccessResponse(
                    "Resource " + result.action() + " to database",
                    result.resourceInfo()
            ));

        } catch (Exception e) {
            log.error("Error syncing with Yandex", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to sync: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // удаление по id в бд
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFileById(@PathVariable Long id) {
        try {
            Object response = fileService.deleteFileById(id, true, true);

            return ResponseEntity.ok(new SuccessResponse(
                    "Resource deleted successfully",
                    response
            ));

        } catch (Exception e) {
            log.error("Error deleting resource", e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to delete resource: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // удаление по полному названию файла
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFileByPath(@RequestBody DeleteRequest request) {
        try {
            Object response = fileService.deleteFileByPath(
                    request.path(),
                    request.permanently(),
                    request.forceAsync()
            );

            return ResponseEntity.ok(new SuccessResponse(
                    "Resource deleted successfully",
                    response
            ));

        } catch (Exception e) {
            log.error("Error deleting resource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to delete resource: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // мягкое удаление (пометить на удаление и в будущем не показывать в общих списках)
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<?> deleteFromDatabase(@PathVariable Long id) {
        try {
            fileService.softDeleteById(id);

            return ResponseEntity.ok(new SuccessResponse(
                    "File marked as deleted successfully",
                    null
            ));

        } catch (Exception e) {
            log.error("Error deleting from database", e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to delete from database: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // мягкое удаление (пометить на удаление и в будущем не показывать в общих списках)
    @DeleteMapping("/soft-delete")
    public ResponseEntity<?> deleteFromDatabaseByPath(@RequestBody Path path) {
        try {
            fileService.softDeleteByPath(path.path());

            return ResponseEntity.ok(new SuccessResponse(
                    "File marked as deleted successfully",
                    null
            ));

        } catch (Exception e) {
            log.error("Error deleting from database", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to delete from database: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // Реальное удаление всех помеченных на удаление
    @DeleteMapping("/clean-soft-delete")
    public ResponseEntity<?> cleanDeleted() {
        try {
            fileService.cleanDeleted();

            return ResponseEntity.ok(new SuccessResponse(
                    "All marked files deleted",
                    null
            ));

        } catch (Exception e) {
            log.error("Error cleaning database", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to clean database: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // восстановить список файлов
    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody Set<String> paths) {
        fileService.restore(paths);
        return ResponseEntity.ok(new SuccessResponse(
                "Files restored successfully",
                null
        ));
    }

    // найти файл по path в сервисе
    @GetMapping("/find-in-service")
    public ResponseEntity<?> getResourceInfo(@RequestBody Path path) {
        try {
            ResourceInfo info = fileService.getResourceInfo(path.path());
            return ResponseEntity.ok(info);

        } catch (Exception e) {
            log.error("Error getting resource info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            "Failed to get resource info: " + e.getMessage(),
                            LocalDateTime.now().toString()
                    ));
        }
    }

    // все файлы в виде дерева
    @GetMapping("/get-full")
    public ResponseEntity<?> getFullTree() {
        try {
            FileTree tree = fileService.getFullTree();
            return ResponseEntity.ok(tree);

        } catch (Exception e) {
            log.error("Error getting full file tree", e);
            return ResponseEntity.status(500).body("Error getting file tree: " + e.getMessage());
        }
    }

    // содержимое папки в виде дерева
    @GetMapping("/get-tree")
    public ResponseEntity<?> getTreeFromPath(@RequestParam(required = false, defaultValue = "/") String path) {
        try {
            FileTree tree = fileService.getTreeFromPath(path);
            return ResponseEntity.ok(tree);

        } catch (Exception e) {
            log.error("Error getting file tree for path: {}", path, e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }

            return ResponseEntity.status(500).body("Error getting file tree: " + e.getMessage());
        }
    }

}

record Path (String path){}