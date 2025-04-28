package ru.back.backend.springJava.contoller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.back.backend.springJava.DTO.TaskSearchDto;
import ru.back.backend.springJava.entity.Task;
import ru.back.backend.springJava.service.TaskService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/task")
public class TaskController {
    public static final String ID_COLUMN = "id"; // имя столбца id

    private TaskService service;
    public TaskController(TaskService service) {this.service = service;}

    @PostMapping("/all")
    public ResponseEntity<List<Task>> findAll(@RequestBody String email) {
        return ResponseEntity.ok(service.findAll(email));
    }

    @PostMapping("/add")
    public ResponseEntity<Task> add(@RequestBody Task task){
        if (task.getId() != null && task.getId() != 0)
            return new ResponseEntity("id must be null",HttpStatus.NOT_ACCEPTABLE);
        if (task.getTitle() == null || task.getTitle().trim().length()==0)
            return new ResponseEntity("title must be NOT null",HttpStatus.NOT_ACCEPTABLE);
        return ResponseEntity.ok( service.add(task));
    }

    @PutMapping("/update")
    public ResponseEntity<Task> update(@RequestBody Task task){
        if (task.getId() == null && task.getId() <= 0)
            return new ResponseEntity("id missed",HttpStatus.NOT_ACCEPTABLE);
        if (task.getTitle() == null || task.getTitle().trim().length()==0)
            return new ResponseEntity("title missed",HttpStatus.NOT_ACCEPTABLE);

        return ResponseEntity.ok( service.update(task));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Task> delete(@PathVariable("id") Long id){
        try {
            service.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id = "+id+" not founded",HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }



    @PostMapping("/id")
    public ResponseEntity<Task> findById(@RequestBody Long id){
        Task task = null;
        try {
            task = service.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("id = "+id+" not founded",HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Task>> findByParam(@RequestBody TaskSearchDto dto) throws ParseException {
        String title            = dto.getTitle()            != null ?  dto.getTitle() : null;
        // Convert Boolean to Integer
        Boolean completed = dto.getCompleted() != null && dto.getCompleted() == 1;
        Long priorityId         = dto.getPriorityId()       != null ? dto.getPriorityId() : null;
        Long categoryId         = dto.getCategoryId()       != null ? dto.getCategoryId() : null;
        String email            = dto.getEmail()            != null ? dto.getEmail() : null;

        Integer pageNumber      = dto.getPageNumber()       != null ? dto.getPageNumber() : null;
        Integer pageSize        = dto.getPageSize()         != null ? dto.getPageSize() : null;

        String sortColumn       = dto.getSortColumn()       != null ? dto.getSortColumn() : null;
        String sortDirection    = dto.getSortDirection()    != null ? dto.getSortDirection() : null;

        if (email == null || email.trim().length()==0)
            return new ResponseEntity("email missed ",HttpStatus.NOT_ACCEPTABLE);


        // Берем даты, суем в Calendar, в котором меняем время
        // достаем оттуда даты обратно

        Date dateFrom = null;
        Date dateTo = null;

        // 00:00 для начальной даты, если она указана
        if (dto.getDateFrom() != null) {
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(dto.getDateFrom());
//            calendarFrom.set(Calendar.DAY_OF_MONTH,-1);
//            calendarFrom.set(Calendar.HOUR_OF_DAY,21);
            calendarFrom.set(Calendar.HOUR_OF_DAY,0);
            calendarFrom.set(Calendar.MINUTE,0);
            calendarFrom.set(Calendar.SECOND,0);
            calendarFrom.set(Calendar.MILLISECOND,0);

            dateFrom = calendarFrom.getTime();
        }

        // 23:59 для конечной даты
        if (dto.getDateTo() != null) {
            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(dto.getDateTo());
            calendarTo.set(Calendar.HOUR_OF_DAY,23);
            calendarTo.set(Calendar.MINUTE,59);
            calendarTo.set(Calendar.SECOND,59);
            calendarTo.set(Calendar.MILLISECOND,999);

            dateTo = calendarTo.getTime();
        }



        // установка направления сортировки asc/desc в спец для этого классе
        Sort.Direction direction = sortDirection == null || sortDirection.trim().length() == 0
                || sortDirection.trim().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // настройка параметра сортировки (направления, какие столбцы)
        Sort sort = Sort.by(direction, sortColumn, ID_COLUMN);

        // параметр для создания page
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize,sort);
        Page<Task> result = service.findByParam(title,completed,priorityId,categoryId,dateFrom,dateTo,email,pageRequest);

        return ResponseEntity.ok(result);
    }
}
