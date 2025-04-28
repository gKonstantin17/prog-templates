package ru.back.backend.springJava.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.back.backend.springproj.entity.Stat;
import ru.back.backend.springproj.service.StatService;

@RestController
// не пишем маршрутизацию для контроллера т.к. всего 1 метод
public class StatController {
    private StatService service;
    public StatController(StatService service) {this.service = service;}

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }
}
