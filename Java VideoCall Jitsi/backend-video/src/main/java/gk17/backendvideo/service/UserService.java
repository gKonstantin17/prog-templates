package gk17.backendvideo.service;

import gk17.backendvideo.model.User;
import gk17.backendvideo.model.UserRole;
import gk17.backendvideo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(String username, String password, UserRole role, String fullName, Long logopedId) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        User.UserBuilder builder = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .fullName(fullName);

        if (logopedId != null) {
            User logoped = userRepository.findById(logopedId)
                    .orElseThrow(() -> new IllegalArgumentException("Logoped not found: " + logopedId));
            builder.logoped(logoped);
        }

        User user = builder.build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findPatientsByLogopedId(Long logopedId) {
        return userRepository.findByLogopedId(logopedId);
    }

    @Transactional(readOnly = true)
    public List<User> findAllLogopeds() {
        return userRepository.findByRole(UserRole.LOGOPED);
    }

    @Transactional(readOnly = true)
    public List<User> findAllPatients() {
        return userRepository.findByRole(UserRole.PATIENT);
    }
}
