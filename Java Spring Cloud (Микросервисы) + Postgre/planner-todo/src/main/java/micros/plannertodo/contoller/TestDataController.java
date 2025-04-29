package micros.plannertodo.contoller;

import micros.plannertodo.service.TestDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/data")
public class TestDataController {
    private final TestDataService service;

    public TestDataController(TestDataService service)
    {
        this.service = service;
    }

    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestBody String userId) {
        service.init(userId);
        return ResponseEntity.ok(true);
    }
}
