package micros.plannertodo.contoller;

import micros.plannerentity.entity.Stat;
import micros.plannertodo.service.StatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
// не пишем маршрутизацию для контроллера т.к. всего 1 метод
public class StatController {
    private StatService service;
    public StatController(StatService service) {this.service = service;}

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody String  id) {
        return ResponseEntity.ok(service.findByEmail(id));
    }
}
