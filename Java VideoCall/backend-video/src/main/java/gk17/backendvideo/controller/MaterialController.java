package gk17.backendvideo.controller;

import gk17.backendvideo.dto.MaterialDTO;
import gk17.backendvideo.model.SessionMaterial;
import gk17.backendvideo.model.User;
import gk17.backendvideo.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/sessions/{sessionId}")
    public ResponseEntity<MaterialDTO> uploadMaterial(
            @PathVariable Long sessionId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        SessionMaterial material = materialService.uploadMaterial(sessionId, file, user);
        MaterialDTO response = new MaterialDTO(
                material.getId(),
                material.getSession().getId(),
                material.getFileName(),
                material.getUploadedAt()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<List<MaterialDTO>> getMaterials(@PathVariable Long sessionId) {
        List<SessionMaterial> materials = materialService.getMaterialsBySessionId(sessionId);
        List<MaterialDTO> response = materials.stream()
                .map(m -> new MaterialDTO(m.getId(), m.getSession().getId(), m.getFileName(), m.getUploadedAt()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        Resource resource = materialService.getMaterialFile(id);

        String contentType = "application/octet-stream";
        String fileName = resource.getFilename();

        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        materialService.deleteMaterial(id, user);
        return ResponseEntity.ok().build();
    }
}
