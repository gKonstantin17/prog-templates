package gk17.backendvideo.repository;

import gk17.backendvideo.model.SessionMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionMaterialRepository extends JpaRepository<SessionMaterial, Long> {
    List<SessionMaterial> findBySessionId(Long sessionId);
}
