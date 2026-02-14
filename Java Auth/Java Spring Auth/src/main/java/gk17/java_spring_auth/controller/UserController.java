package gk17.java_spring_auth.controller;

import gk17.java_spring_auth.dto.UserDto;
import gk17.java_spring_auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile")
    public Map<String, Object> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();
        UserDto user = userService.getUserByLogin(login);

        return Map.of(
                "user", user,
                "authenticated", true,
                "message", "Ваш профиль"
        );
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('User')")
    @Operation(summary = "User dashboard (only for USER role)")
    public Map<String, String> getUserDashboard() {
        return Map.of(
                "message", "Добро пожаловать в пользовательскую панель",
                "features", "Просмотр профиля, редактирование",
                "role", "USER"
        );
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check authentication status")
    public Map<String, Object> getStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        return Map.of(
                "authenticated", true,
                "username", auth.getName(),
                "isAdmin", isAdmin,
                "isUser", isUser,
                "authorities", auth.getAuthorities()
        );
    }
}
