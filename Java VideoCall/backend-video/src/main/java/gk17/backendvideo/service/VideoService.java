package gk17.backendvideo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис-фасад для работы с видеоконференциями.
 * В текущей версии делегирует вызовы JitsiTokenService.
 */
@Service
@RequiredArgsConstructor
public class VideoService {

    private final JitsiTokenService jitsiTokenService;

    /**
     * Генерирует JWT токен для подключения к комнате Jitsi
     * 
     * @param user пользователь
     * @param roomName имя комнаты
     * @return JWT токен
     */
    public String generateToken(gk17.backendvideo.model.User user, String roomName) {
        // Логопед всегда модератор, пациент - нет
        boolean isModerator = user.getRole() == gk17.backendvideo.model.UserRole.LOGOPED;
        return jitsiTokenService.generateToken(user, roomName, isModerator);
    }

    /**
     * Возвращает URL для IFrame Jitsi
     */
    public String getJitsiUrl() {
        return jitsiTokenService.getJitsiDomain();
    }

    /**
     * Возвращает домен Jitsi для IFrame API
     */
    public String getJitsiDomain() {
        return jitsiTokenService.getJitsiDomain();
    }
}
