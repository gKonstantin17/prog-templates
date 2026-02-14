package gk17.java_spring_auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management endpoints")
@PreAuthorize("hasRole('Admin')") // Все методы требуют роль ADMIN
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard")
    public Map<String, String> getDashboard() {
        return Map.of(
                "message", "Добро пожаловать в панель администратора",
                "status", "active",
                "role", "Admin"
        );
    }

    @GetMapping("/stats")
    @Operation(summary = "Get system statistics")
    public Map<String, Object> getStats() {
        return Map.of(
                "totalUsers", 150,
                "activeUsers", 42,
                "totalPosts", 325,
                "serverTime", System.currentTimeMillis()
        );
    }

    @PostMapping("/settings")
    @Operation(summary = "Update system settings")
    public Map<String, String> updateSettings(@RequestBody Map<String, Object> settings) {
        return Map.of(
                "message", "Настройки обновлены",
                "updated", settings.toString()
        );
    }
}
