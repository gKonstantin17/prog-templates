package gk17.backendvideo.controller;

import gk17.backendvideo.dto.CreateSessionRequest;
import gk17.backendvideo.dto.JoinTokenResponse;
import gk17.backendvideo.dto.SessionAccessDTO;
import gk17.backendvideo.dto.SessionDTO;
import gk17.backendvideo.dto.UserDTO;
import gk17.backendvideo.model.Session;
import gk17.backendvideo.model.User;
import gk17.backendvideo.service.SessionAccessStatus;
import gk17.backendvideo.service.SessionService;
import gk17.backendvideo.service.UserService;
import gk17.backendvideo.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SessionController {

    private final SessionService sessionService;
    private final VideoService videoService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getSessions(@AuthenticationPrincipal User user) {
        List<SessionDTO> sessions = sessionService.getSessionsForUser(user);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<SessionDTO> createSession(
            @AuthenticationPrincipal User user,
            @RequestBody CreateSessionRequest request) {
        SessionDTO session = sessionService.createSession(user.getId(), request);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable Long id) {
        SessionDTO session = sessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<SessionAccessDTO> getSessionStatus(@PathVariable Long id) {
        Session session = sessionService.getSessionById(id).toSession();
        SessionAccessStatus status = sessionService.getSessionAccessStatus(session);
        boolean canJoin = status == SessionAccessStatus.ALLOWED;

        SessionAccessDTO response = new SessionAccessDTO(id, status.name(), canJoin);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SessionDTO> completeSession(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        SessionDTO session = sessionService.completeSession(id, user.getId());
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{id}/join-token")
    public ResponseEntity<JoinTokenResponse> getJoinToken(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        // Проверка доступа к сессии
        if (!sessionService.hasAccessToSession(id, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this session");
        }

        Session session = sessionService.getSessionEntityById(id);

        // Проверка временного окна
        if (!sessionService.canJoinSession(session)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Session is not available for joining");
        }

        String token = videoService.generateToken(user, session.getRoomName());
        String url = videoService.getJitsiDomain();

        JoinTokenResponse response = new JoinTokenResponse(url, token, session.getRoomName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<UserDTO>> getLogopedPatients(@AuthenticationPrincipal User user) {
        List<User> patients = userService.findPatientsByLogopedId(user.getId());
        List<UserDTO> response = patients.stream()
                .map(p -> new UserDTO(p.getId(), p.getUsername(), p.getFullName(), p.getRole().name()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
