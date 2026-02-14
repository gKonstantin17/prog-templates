package gk17.java_spring_auth.controller;

import gk17.java_spring_auth.dto.AuthResponse;
import gk17.java_spring_auth.dto.LoginDto;
import gk17.java_spring_auth.dto.RegisterDto;
import gk17.java_spring_auth.dto.UserDto;
import gk17.java_spring_auth.entity.User;
import gk17.java_spring_auth.service.AuthService;
import gk17.java_spring_auth.service.UserService;
import gk17.java_spring_auth.utils.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Регистрация нового пользователя: {}", registerDto.login());
        UserDto createdUser = userService.create(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Вход в систему
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletResponse response) {
        log.info("Вход в систему: {}", loginDto.login());
        AuthResponse authResponse = authService.login(loginDto, response);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Обновление токенов
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null) {
            throw new RuntimeException("Refresh токен не найден");
        }

        log.info("Обновление токенов");
        AuthResponse authResponse = authService.refreshTokens(refreshToken, response);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Выход из системы
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Получаем логин из SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth != null ? auth.getName() : null;

        log.info("Выход из системы: {}", login);
        authService.logout(login, response);

        return ResponseEntity.ok().build();
    }

    /**
     * Проверка токена
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            String login = jwtProvider.getLoginFromToken(accessToken);
            User user = userService.findByLogin(login);
            return ResponseEntity.ok().body(Map.of("user", user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
    }

    /**
     * Извлечение access токена из куки
     */
    private String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "access_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Извлечение refresh токена из куки
     */
    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "refresh_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
