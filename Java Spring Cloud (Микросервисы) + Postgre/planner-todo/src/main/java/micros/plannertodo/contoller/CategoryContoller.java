package micros.plannertodo.contoller;

import micros.plannerentity.entity.User;
import micros.plannertodo.dto.CategorySearchDto;
import micros.plannertodo.feign.UserFeignClient;
import micros.plannertodo.service.CategoryService;
import micros.plannerutils.rest.resttemplate.UserRestBuilder;
import micros.plannerutils.rest.webclient.UserWebClientBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import micros.plannerentity.entity.Category;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/category")
public class CategoryContoller {
    private CategoryService service;
   // private UserRestBuilder userRest;
    private UserWebClientBuilder userWeb;
    private UserFeignClient userFeignClient;

    public CategoryContoller(CategoryService service,
                             //UserRestBuilder userRest,
                             UserWebClientBuilder userWeb,
                             UserFeignClient userFeignClient)
    {
        this.service = service;
        //this.userRest = userRest;
        this.userWeb = userWeb;
        this.userFeignClient = userFeignClient;
    }

    @PostMapping("/all")
    public List<Category> findAll(@RequestBody String id) {
        return service.findAll(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Category> add(@RequestBody Category category, @AuthenticationPrincipal Jwt jwt) {
        category.setUserId(jwt.getSubject()); // UUID из KeyCloak


        // id != null and != 0 - error because id write automatically
        if (category.getId() != null && category.getId() != 0)
            return new ResponseEntity("redundant param: id MUST be null",
                    HttpStatus.NOT_ACCEPTABLE);
        // title = null - error because title must be writen
        // trim - string without space
        if (category.getTitle() == null || category.getTitle().trim().length() == 0)
            return new ResponseEntity("missed param: title MUST be not null",
                    HttpStatus.NOT_ACCEPTABLE);



//  убираем проверку на существование пользователя
//        if (userFeignClient.findUserById(category.getUserId()) != null)
//            return ResponseEntity.ok(service.add(category));

        if (!category.getUserId().isBlank()) // если не пустой
            return ResponseEntity.ok(service.add(category));

        return new ResponseEntity("user id = "+ category.getUserId()+" not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchDto catSearDto, @AuthenticationPrincipal Jwt jwt) {
        catSearDto.setUserId(jwt.getSubject());

        if (catSearDto.getUserId().isBlank())
            return new ResponseEntity("param id missed ", HttpStatus.NOT_ACCEPTABLE);

        List<Category> list = service.findByTitle(catSearDto.getTitle(), catSearDto.getUserId());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/id")
    public ResponseEntity<Category> findById(@RequestBody Long id) {
        Category category = null;
        try {
            category = service.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("id= "+id+" not founded",HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(category);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Category category) {
        if (category.getId() == null || category.getId() < 0) {
            return new ResponseEntity("missed param id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param id", HttpStatus.NOT_ACCEPTABLE);
        }
        service.update(category);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            service.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id= "+id+ " not found", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
