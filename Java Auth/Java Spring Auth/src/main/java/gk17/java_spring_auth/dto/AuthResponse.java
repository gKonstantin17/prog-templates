package gk17.java_spring_auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,  // в секундах

        @JsonProperty("expires_at")
        String expiresAt, // человекочитаемое время

        @JsonProperty("user")
        UserDto user
) {
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserDto user) {
        this(
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn,
                LocalDateTime.now().plusSeconds(expiresIn)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                user
        );
    }

    private static String formatExpiryDate(Long expiresIn) {
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(expiresIn);
        return expiryTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
