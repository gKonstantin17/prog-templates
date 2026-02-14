package gk17.java_spring_auth.dto;

public record UserDto(
        Long id,
        String name,
        String login,
        String role
) {}