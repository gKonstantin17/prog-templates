package gk17.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/yokassa")
public class YokassaController {

    private static final Logger log = LoggerFactory.getLogger(YokassaController.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public YokassaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping("/notification")
    public ResponseEntity<Void> getNotification(
            @RequestBody String requestBody) {

        log.info("=== Received webhook notification ===");

        try {
            // 1. Выводим сырое тело запроса
            log.info("Raw request body: {}", requestBody);

            JsonNode root = objectMapper.readTree(requestBody);

            // 4. Разбираем основные поля (для отладки)
            String event = root.has("event") ? root.get("event").asText() : "unknown";
            log.info("Event type: {}", event);

            JsonNode objectNode = root.get("object");
            log.info("Object ID: {}", objectNode.get("id").asText());
            log.info("Object status: {}", objectNode.get("status").asText());
            JsonNode amount = objectNode.get("amount");
            log.info("Amount: {} {}", amount.get("value").asText(), amount.get("currency").asText());
            if (root.has("object")) {

                if (objectNode.has("metadata") && objectNode.get("metadata").has("order_id")) {
                    log.info("Order ID from metadata: {}", objectNode.get("metadata").get("order_id").asText());
                }
            }

            // 5. Здесь будет логика обновления статуса заказа
            // processPaymentStatus(root);

        } catch (IOException e) {
            log.error("Failed to parse webhook notification", e);
        }

        // Всегда возвращаем 200 OK
        return ResponseEntity.ok().build();
    }
}
