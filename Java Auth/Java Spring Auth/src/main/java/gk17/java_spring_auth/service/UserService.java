package gk17.java_spring_auth.service;

import gk17.java_spring_auth.entity.Role;
import gk17.java_spring_auth.entity.User;
import gk17.java_spring_auth.dto.LoginDto;
import gk17.java_spring_auth.dto.RegisterDto;
import gk17.java_spring_auth.dto.UserDto;
import gk17.java_spring_auth.repository.RoleRepository;
import gk17.java_spring_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public UserDto create(RegisterDto dto) {
        if (repository.existsByLogin(dto.login()))
            return null;
        Role role = roleRepository.findByName("User").get();


        User user = new User();
        user.setName(dto.name());
        user.setLogin(dto.login());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(role);
        var result = repository.save(user);
        return new UserDto(result.getId(), result.getName(), result.getLogin(), result.getRole().getName());
    }

    public UserDto login(LoginDto dto) {
        Optional<User> result = repository.findByLogin(dto.login());
        User user;
        if (result.isPresent())
            user = result.get();
        else return null;
        //                      не хешировано,      захешировано
        if (passwordEncoder.matches(dto.password(), user.getPassword()))
            return new UserDto(user.getId(), user.getName(), user.getLogin(), user.getRole().getName());
        else return null;
    }

    @Transactional(readOnly = true)
    public UserDto getUserByLogin(String login) {
        User user = repository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getRole().getName()
        );
    }

    public User findByLogin(String login) {
        return repository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + login));
    }
}
