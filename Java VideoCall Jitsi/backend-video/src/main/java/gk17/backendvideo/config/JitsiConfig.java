package gk17.backendvideo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jitsi")
public class JitsiConfig {

    /**
     * URL сервера Jitsi Meet (для IFrame API)
     */
    private String url;

    /**
     * JWT настройки для авторизации в Jitsi
     */
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        /**
         * Application ID для JWT токенов
         */
        private String appId;

        /**
         * Secret key для подписи JWT токенов (минимум 32 символа)
         */
        private String appSecret;
    }
}
