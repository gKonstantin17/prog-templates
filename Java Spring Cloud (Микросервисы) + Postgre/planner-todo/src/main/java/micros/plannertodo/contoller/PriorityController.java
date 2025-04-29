package micros.plannertodo.contoller;

import micros.plannerentity.entity.Priority;
import micros.plannertodo.dto.PrioritySearchDto;
import micros.plannertodo.feign.UserFeignClient;
import micros.plannertodo.service.PriorityService;
import micros.plannerutils.rest.resttemplate.UserRestBuilder;
import micros.plannerutils.rest.webclient.UserWebClientBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")
public class PriorityController {
    PriorityService service;
   // private UserRestBuilder userRest;
    private UserWebClientBuilder userWeb;
    private UserFeignClient userFeignClient;
    public PriorityController(PriorityService service,
                             // UserRestBuilder userRest,
                              UserWebClientBuilder userWeb,
                              UserFeignClient userFeignClient)
    {
        this.service = service;
        //this.userRest = userRest;
        this.userWeb = userWeb;
        this.userFeignClient = userFeignClient;
    }

    @PostMapping("/all")
    public ResponseEntity<List<Priority>> findAll(@RequestBody String id) {
        return ResponseEntity.ok(service.findAll(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority, @AuthenticationPrincipal Jwt jwt) {
        priority.setUserId(jwt.getSubject()); // UUID из KeyCloak

        if (priority.getId() != null && priority.getId() != 0)
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0)
            return new ResponseEntity("redundant param: title MUST be not null", HttpStatus.NOT_ACCEPTABLE);
        if (priority.getColor() == null || priority.getColor().trim().length() == 0)
            return new ResponseEntity("redundant param: id MUST be not null", HttpStatus.NOT_ACCEPTABLE);

//        if (userFeignClient.findUserById(priority.getUserId()) != null)
//            return ResponseEntity.ok(service.add(priority));
        if (!priority.getUserId().isBlank()) // если не пустой
            return ResponseEntity.ok(service.add(priority));

        return new ResponseEntity("user id = "+ priority.getUserId()+" not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/id")
    public ResponseEntity<Priority> findById(@RequestBody Long id) {
        Priority priority = null;
        try {
            priority = service.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("id= "+id+" not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priority);
    }

    @PutMapping("/update")
    public ResponseEntity<Priority> update(@RequestBody Priority priority) {
        if (priority.getId() == null || priority.getId() <= 0) {
            return new ResponseEntity("param id missed ", HttpStatus.NOT_ACCEPTABLE);
        }
        if (priority.getTitle() == null || priority.getTitle().trim().length() <= 0)
            return new ResponseEntity("param title missed ", HttpStatus.NOT_ACCEPTABLE);
        if (priority.getColor() == null || priority.getColor().trim().length() <= 0)
            return new ResponseEntity("param color missed ", HttpStatus.NOT_ACCEPTABLE);
        service.update(priority);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Priority> delete(@PathVariable("id") Long id){
        try {
            service.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id not found ", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchDto dto, @AuthenticationPrincipal Jwt jwt) {
        dto.setUserId(jwt.getSubject());

        if (dto.getUserId().isBlank())
            return new ResponseEntity("param id missed ", HttpStatus.NOT_ACCEPTABLE);

        return ResponseEntity.ok(service.find(dto.getTitle(),dto.getUserId()));
    }
}
