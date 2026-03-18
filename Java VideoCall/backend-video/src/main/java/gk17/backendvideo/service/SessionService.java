package gk17.backendvideo.service;

import gk17.backendvideo.dto.CreateSessionRequest;
import gk17.backendvideo.dto.SessionDTO;
import gk17.backendvideo.model.*;
import gk17.backendvideo.repository.SessionRepository;
import gk17.backendvideo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SessionDTO> getSessionsForUser(User currentUser) {
        List<Session> sessions;

        if (currentUser.getRole() == UserRole.LOGOPED) {
            sessions = sessionRepository.findByLogopedId(currentUser.getId());
        } else {
            sessions = sessionRepository.findByParticipantsPatientId(currentUser.getId());
        }

        return sessions.stream().map(this::toDTO).toList();
    }

    @Transactional
    public SessionDTO createSession(Long logopedId, CreateSessionRequest request) {
        User logoped = userRepository.findById(logopedId)
                .orElseThrow(() -> new IllegalArgumentException("Logoped not found"));

        Session session = Session.builder()
                .logoped(logoped)
                .title(request.getTitle())
                .roomName(generateRoomName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SessionStatus.SCHEDULED)
                .build();

        if (request.getPatientIds() != null) {
            for (Long patientId : request.getPatientIds()) {
                User patient = userRepository.findById(patientId)
                        .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

                if (patient.getRole() != UserRole.PATIENT) {
                    throw new IllegalArgumentException("User is not a patient: " + patientId);
                }

                if (patient.getLogoped() == null || !patient.getLogoped().getId().equals(logopedId)) {
                    throw new IllegalArgumentException("Patient does not belong to this logoped");
                }

                SessionParticipant participant = new SessionParticipant();
                participant.setSession(session);
                participant.setPatient(patient);
                session.addParticipant(participant);
            }
        }

        Session saved = sessionRepository.save(session);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public SessionDTO getSessionById(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        return toDTO(session);
    }

    @Transactional(readOnly = true)
    public Session getSessionEntityById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    @Transactional(readOnly = true)
    public boolean hasAccessToSession(Long sessionId, User user) {
        Session session = getSessionEntityById(sessionId);

        if (user.getRole() == UserRole.LOGOPED) {
            return session.getLogoped().getId().equals(user.getId());
        } else {
            return session.getParticipants().stream()
                    .anyMatch(p -> p.getPatient().getId().equals(user.getId()));
        }
    }

    @Transactional
    public SessionDTO completeSession(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getLogoped().getId().equals(userId)) {
            session.setStatus(SessionStatus.COMPLETED);
            session = sessionRepository.save(session);
        } else {
            throw new IllegalArgumentException("Only logoped can complete session");
        }

        return toDTO(session);
    }

    public boolean canJoinSession(Session session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = session.getStartTime().minusMinutes(15);
        LocalDateTime windowEnd = session.getEndTime();

        // Для тестирования - всегда разрешаем вход
        // return (now.isEqual(windowStart) || now.isAfter(windowStart)) &&
        //        (now.isEqual(windowEnd) || now.isBefore(windowEnd));
        
        return session.getStatus() != SessionStatus.COMPLETED && 
               session.getStatus() != SessionStatus.CANCELLED;
    }

    public SessionAccessStatus getSessionAccessStatus(Session session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = session.getStartTime().minusMinutes(15);

        if (now.isBefore(windowStart)) {
            return SessionAccessStatus.WAITING;
        } else if (now.isAfter(session.getEndTime())) {
            return SessionAccessStatus.ENDED;
        } else {
            return SessionAccessStatus.ALLOWED;
        }
    }

    private String generateRoomName() {
        return "room-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private SessionDTO toDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setTitle(session.getTitle());
        dto.setRoomName(session.getRoomName());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setStatus(session.getStatus().name());
        dto.setLogopedId(session.getLogoped().getId());
        dto.setLogopedName(session.getLogoped().getFullName());

        if (session.getParticipants() != null) {
            dto.setPatientIds(session.getParticipants().stream()
                    .map(p -> p.getPatient().getId())
                    .toList());
        }

        dto.setCanJoin(canJoinSession(session));

        return dto;
    }
}
