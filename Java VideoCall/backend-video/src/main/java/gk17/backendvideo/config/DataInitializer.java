package gk17.backendvideo.config;

import gk17.backendvideo.model.User;
import gk17.backendvideo.model.UserRole;
import gk17.backendvideo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        // Проверяем, есть ли уже пользователи
        if (userService.findAllLogopeds().isEmpty()) {
            log.info("Initializing test data...");

            // Создаем логопеда
            User logoped = userService.createUser(
                    "logoped",
                    "password",
                    UserRole.LOGOPED,
                    "Доктор Логопедов",
                    null
            );
            log.info("Created logoped: {}", logoped.getUsername());

            // Создаем пациентов
            User patient1 = userService.createUser(
                    "patient1",
                    "password",
                    UserRole.PATIENT,
                    "Пациент Один",
                    logoped.getId()
            );
            log.info("Created patient: {}", patient1.getUsername());

            User patient2 = userService.createUser(
                    "patient2",
                    "password",
                    UserRole.PATIENT,
                    "Пациент Два",
                    logoped.getId()
            );
            log.info("Created patient: {}", patient2.getUsername());

            log.info("Test data initialization completed!");
        } else {
            log.info("Test data already exists, skipping initialization");
        }
    }
}
