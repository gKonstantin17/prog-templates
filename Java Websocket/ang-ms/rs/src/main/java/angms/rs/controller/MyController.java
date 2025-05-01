package angms.rs.controller;

import angms.rs.dto.MyEvent;
import angms.rs.model.Result;
import angms.rs.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/data")
public class MyController {
    private static final RestTemplate restTemplate = new RestTemplate(); // для выполнения веб запросов на KeyCloak

    @GetMapping("/test")
    public ResponseEntity<Result> test() {
        return ResponseEntity.ok(new Result("ho-ho-ho, u get data"));
    }


    @Autowired
    private EventService eventService;
    @PostMapping("/event")
    public ResponseEntity checkEvent(@RequestBody MyEvent event) {
        System.out.println("!!! Event from BACKEND " + event.getDescr());
        eventService.addEvent(event);
        return ResponseEntity.ok().build();
    }

}
