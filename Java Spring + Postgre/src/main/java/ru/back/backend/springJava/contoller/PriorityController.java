package ru.back.backend.springJava.contoller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.back.backend.springproj.DTO.PrioritySearchDto;
import ru.back.backend.springproj.entity.Priority;
import ru.back.backend.springproj.service.PriorityService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")
public class PriorityController {
    PriorityService service;
    public PriorityController(PriorityService service) {this.service = service;}

    @PostMapping("/all")
    public ResponseEntity<List<Priority>> findAll(@RequestBody String email) {
        return ResponseEntity.ok(service.findAll(email));
    }

    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority) {
        if (priority.getId() != null && priority.getId() != 0)
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0)
            return new ResponseEntity("redundant param: title MUST be not null", HttpStatus.NOT_ACCEPTABLE);
        if (priority.getColor() == null || priority.getColor().trim().length() == 0)
            return new ResponseEntity("redundant param: id MUST be not null", HttpStatus.NOT_ACCEPTABLE);
        return ResponseEntity.ok(service.add(priority));
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
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchDto dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().length() == 0)
            return new ResponseEntity("param email missed ", HttpStatus.NOT_ACCEPTABLE);

        return ResponseEntity.ok(service.find(dto.getTitle(),dto.getEmail()));
    }
}
