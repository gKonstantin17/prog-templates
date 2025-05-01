package back.backend.controller;

import back.backend.dto.DataDto;
import back.backend.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/data")
    public ResponseEntity<DataDto> user(@RequestBody UserDto userData) {
        System.out.println("email: " + userData.getEmail());
        DataDto dataResult = new DataDto();

        Random random = new Random();
        dataResult.setFollowers(random.nextInt(100));
        dataResult.setFollowing(random.nextInt(1000));
        return ResponseEntity.ok(dataResult);
    }
    @GetMapping("/da")
    public String da() {
        return "ad";
    }
}
