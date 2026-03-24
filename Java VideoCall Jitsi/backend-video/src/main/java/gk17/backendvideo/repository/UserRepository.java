package gk17.backendvideo.repository;

import gk17.backendvideo.model.User;
import gk17.backendvideo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRole(UserRole role);
    List<User> findByLogopedId(Long logopedId);
}
