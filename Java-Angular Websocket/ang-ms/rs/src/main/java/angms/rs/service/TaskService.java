package angms.rs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    private EventService eventService;

    @Scheduled(cron = "0 * * * * ?", zone = "Europe/Moscow") // каждую минуту
    public void checkEvent() {
        System.out.println("\nЯ тут жду\n");
        eventService.checkUpcomingEvents();
    }
}
