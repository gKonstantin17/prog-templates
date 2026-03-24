package gk17.backendvideo.repository;

import gk17.backendvideo.model.Session;
import gk17.backendvideo.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByLogopedId(Long logopedId);
    List<Session> findByParticipantsPatientId(Long patientId);
    List<Session> findByLogopedIdAndStatus(Long logopedId, SessionStatus status);
    List<Session> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
