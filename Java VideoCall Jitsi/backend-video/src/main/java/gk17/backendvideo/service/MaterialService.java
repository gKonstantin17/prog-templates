package gk17.backendvideo.service;

import gk17.backendvideo.model.Session;
import gk17.backendvideo.model.SessionMaterial;
import gk17.backendvideo.model.User;
import gk17.backendvideo.model.UserRole;
import gk17.backendvideo.repository.SessionMaterialRepository;
import gk17.backendvideo.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final SessionRepository sessionRepository;
    private final SessionMaterialRepository materialRepository;

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Transactional
    public SessionMaterial uploadMaterial(Long sessionId, MultipartFile file, User user) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        // Только логопед может загружать материалы
        if (user.getRole() != UserRole.LOGOPED || !session.getLogoped().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only session logoped can upload materials");
        }

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).resolve(String.valueOf(sessionId));

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            SessionMaterial material = SessionMaterial.builder()
                    .session(session)
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .build();

            session.addMaterial(material);
            return materialRepository.save(material);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Transactional(readOnly = true)
    public List<SessionMaterial> getMaterialsBySessionId(Long sessionId) {
        return materialRepository.findBySessionId(sessionId);
    }

    @Transactional(readOnly = true)
    public Resource getMaterialFile(Long materialId) {
        SessionMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found: " + materialId));

        try {
            Path filePath = Paths.get(material.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + materialId);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + materialId, e);
        }
    }

    @Transactional
    public void deleteMaterial(Long materialId, User user) {
        SessionMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found: " + materialId));

        // Только логопед может удалять материалы
        if (user.getRole() != UserRole.LOGOPED || 
            !material.getSession().getLogoped().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only session logoped can delete materials");
        }

        // Удаляем файл с диска
        try {
            Path filePath = Paths.get(material.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Игнорируем ошибку удаления файла
        }

        materialRepository.delete(material);
    }
}
