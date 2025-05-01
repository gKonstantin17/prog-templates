package angms.rs.service;




import angms.rs.dto.MyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private Map<String, MyEvent> eventsStorage = new ConcurrentHashMap<>();

    private static final RestTemplate restTemplate = new RestTemplate();
    public void addEvent(MyEvent event) {
        String eventId = "event_" + System.currentTimeMillis();
        eventsStorage.put(eventId, event);
    }
    public List<MyEvent> getAllEvents() {
        return new ArrayList<>(eventsStorage.values());
    }
    public void checkUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusMinutes(5);
        System.out.println("NOW: "+now);
        System.out.println("SOON: "+ soon);

        eventsStorage.values().forEach(event -> {
            if (event.getDate().isAfter(now) && event.getDate().isBefore(soon)) {
                System.out.println("[SCHEDULER] Событие скоро: " + event.getDescr() +
                        " (в " + event.getDate() + ")");

                // Можно добавить отправку уведомления через WebSocket
                String descr = event.getDescr();
                Duration duration = Duration.between(now, event.getDate());
                MyEvent eventToSend;
                if (duration.toMinutes() == 0) {
                     eventToSend = new MyEvent(
                            LocalDateTime.now(), descr+ " наступило!"
                    );
                }
                else {
                    eventToSend = new MyEvent(
                            LocalDateTime.now(), "Наступит через "+duration.toMinutes()+" минут: "+descr
                    );
                }

                sendTask(eventToSend);
//                messagingTemplate.convertAndSend("/topic/messages", eventToSend);
                System.out.println("Send notification: " + eventToSend);
            }
        });


    }

    private void sendTask(MyEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Оборачиваем тело в HttpEntity
        HttpEntity<MyEvent> entity = new HttpEntity<>(event, headers);

        // Отправляем POST-запрос
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8380/forevent/event",
                HttpMethod.POST,
                entity,
                String.class
        );
        if (response.getStatusCode()== HttpStatus.OK) {
            System.out.println("ОКей");
        }
    }
}
