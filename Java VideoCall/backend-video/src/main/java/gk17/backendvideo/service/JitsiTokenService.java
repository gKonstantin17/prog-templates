package gk17.backendvideo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import gk17.backendvideo.config.JitsiConfig;
import gk17.backendvideo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для генерации JWT токенов для авторизации в Jitsi Meet
 * 
 * Спецификация JWT для Jitsi:
 * - iss: JWT_APP_ID
 * - sub: user ID
 * - aud: JWT_APP_ID
 * - exp: время истечения
 * - context.user.email: email пользователя
 * - context.user.name: имя пользователя
 * - context.user.moderator: true для логопеда
 */
@Service
@RequiredArgsConstructor
public class JitsiTokenService {

    private final JitsiConfig jitsiConfig;

    /**
     * Генерирует JWT токен для подключения к комнате Jitsi
     * 
     * @param user пользователь
     * @param roomName имя комнаты
     * @param isModerator является ли пользователь модератором (логопед)
     * @return JWT токен
     */
    public String generateToken(User user, String roomName, boolean isModerator) {
        Algorithm algorithm = Algorithm.HMAC256(jitsiConfig.getJwt().getAppSecret());

        // Контекст пользователя для Jitsi
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        // Используем username как email (т.к. поля email в User нет)
        userInfo.put("email", user.getUsername() + "@logopedic.local");
        userInfo.put("name", user.getFullName() != null ? user.getFullName() : user.getUsername());
        userInfo.put("moderator", isModerator);
        context.put("user", userInfo);

        // Создаем токен
        return JWT.create()
            .withIssuer(jitsiConfig.getJwt().getAppId())
            .withAudience(jitsiConfig.getJwt().getAppId())
            .withSubject(String.valueOf(user.getId()))
            .withClaim("context", context)
            .withClaim("room", roomName)
            .withExpiresAt(new Date(System.currentTimeMillis() + 7200 * 1000)) // 2 часа
            .sign(algorithm);
    }

    /**
     * Возвращает URL для IFrame Jitsi
     * Формат: {url}/{roomName}#config.seed=...
     */
    public String getJitsiUrl(String roomName) {
        String baseUrl = jitsiConfig.getUrl();
        // Убираем trailing slash если есть
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/" + roomName;
    }

    /**
     * Возвращает базовый URL Jitsi для IFrame API
     * Извлекает домен из URL (убирает http:// или https://)
     */
    public String getJitsiDomain() {
        String url = jitsiConfig.getUrl();
        // Извлекаем домен из URL (убираем http:// или https://)
        if (url.startsWith("https://")) {
            return url.substring(8);
        }
        if (url.startsWith("http://")) {
            return url.substring(7);
        }
        return url;
    }
}
