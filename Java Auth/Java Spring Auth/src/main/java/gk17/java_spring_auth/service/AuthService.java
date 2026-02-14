package gk17.java_spring_auth.service;

import gk17.java_spring_auth.dto.AuthResponse;
import gk17.java_spring_auth.dto.LoginDto;
import gk17.java_spring_auth.dto.UserDto;
import gk17.java_spring_auth.entity.User;
import gk17.java_spring_auth.repository.UserRepository;
import gk17.java_spring_auth.utils.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${domain.url}")
    private String domainUrl;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентификация пользователя и генерация токенов
     */
    @Transactional
    public AuthResponse login(LoginDto loginDto, HttpServletResponse response) {
        // 1. Ищем пользователя
        User user = userRepository.findByLogin(loginDto.login())
                .orElseThrow(() -> new RuntimeException("Неверный логин или пароль"));

        // 2. Проверяем пароль
        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new RuntimeException("Неверный логин или пароль");
        }

        // 3. Получаем роль
        String roleName = user.getRole() != null ? user.getRole().getName() : "User";

        // 4. Генерируем токены
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getLogin(), roleName);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getLogin());

        // 5. Сохраняем refresh token в БД (timestamp)
        LocalDateTime expiryDateTime = LocalDateTime.now().plusDays(7);
        Timestamp expiryDate = Timestamp.valueOf(expiryDateTime);

        user.setRefreshTokenHash(passwordEncoder.encode(refreshToken));
        user.setRefreshTokenExpiryDate(expiryDate);
        userRepository.save(user);

        // 6. Устанавливаем куки
        setTokenCookies(response, accessToken, refreshToken);

        // 7. Создаем UserDto для ответа
        UserDto userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getLogin(),
                roleName
        );

        // 8. Возвращаем ответ
        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProvider.getExpiration() / 1000, // конвертируем в секунды
                userDto
        );
    }

    /**
     * Обновление токенов по refresh токена
     */
    @Transactional
    public AuthResponse refreshTokens(String refreshToken, HttpServletResponse response) {
        // 1. Проверяем валидность refresh токена
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Невалидный refresh токен");
        }

        // 2. Получаем информацию из токена
        String login = jwtProvider.getLoginFromToken(refreshToken);
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        // 3. Ищем пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 4. Проверяем refresh token в БД
        if (user.getRefreshTokenHash() == null ||
                !passwordEncoder.matches(refreshToken, user.getRefreshTokenHash())) {
            throw new RuntimeException("Refresh токен не найден");
        }

        // 5. Проверяем срок действия (сравниваем Timestamp)
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        if (user.getRefreshTokenExpiryDate().before(now)) {
            // Токен истек - очищаем
            user.setRefreshTokenHash(null);
            user.setRefreshTokenExpiryDate(null);
            userRepository.save(user);
            throw new RuntimeException("Refresh токен истек");
        }

        // 6. Генерируем новые токены
        String roleName = user.getRole() != null ? user.getRole().getName() : "User";
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getLogin(), roleName);
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getLogin());

        // 7. Обновляем refresh token в БД
        LocalDateTime newExpiryDateTime = LocalDateTime.now().plusDays(7);
        Timestamp newExpiryDate = Timestamp.valueOf(newExpiryDateTime);

        user.setRefreshTokenHash(passwordEncoder.encode(newRefreshToken));
        user.setRefreshTokenExpiryDate(newExpiryDate);
        userRepository.save(user);

        // 8. Устанавливаем новые куки
        setTokenCookies(response, newAccessToken, newRefreshToken);

        // 9. Создаем UserDto
        UserDto userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getLogin(),
                roleName
        );

        // 10. Возвращаем ответ
        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtProvider.getExpiration() / 1000,
                userDto
        );
    }

    /**
     * Выход из системы
     */
    @Transactional
    public void logout(String login, HttpServletResponse response) {
        if (login != null && !login.isEmpty()) {
            User user = userRepository.findByLogin(login)
                    .orElse(null);

            if (user != null) {
                // Удаляем refresh token из БД
                user.setRefreshTokenHash(null);
                user.setRefreshTokenExpiryDate(null);
                userRepository.save(user);
            }
        }

        // Очищаем куки
        clearTokenCookies(response);
    }

    /**
     * Установка куки с токенами
     */
    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Получаем точное время жизни из токенов
        long accessMaxAge = jwtProvider.getExpirationSecondsFromNow(accessToken);
        long refreshMaxAge = jwtProvider.getExpirationSecondsFromNow(refreshToken);

        log.debug("Токены: access протухнет через {} секунд, refresh через {} секунд",
                accessMaxAge, refreshMaxAge);

        // Access cookie
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(domainUrl)
                .maxAge(accessMaxAge) // точное время!
                .sameSite("None")
                .build();

        // Refresh cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(domainUrl)
                .maxAge(refreshMaxAge) // точное время!
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        log.debug("Установлены cookies: access_token ({}s), refresh_token ({}s)", accessMaxAge, refreshMaxAge);
    }



    private void clearTokenCookies(HttpServletResponse response) {
        // Access token
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(domainUrl)
                .maxAge(0)
                .sameSite("None")
                .build();

        // Refresh token
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(domainUrl)
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        log.debug("Очищены cookies");
    }
}