package micros.plannerusers.controller;


import micros.plannerusers.dto.UserKcDTO;
import micros.plannerusers.keycloak.KeycloakUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.UserRepresentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/user")
public class AdminController {
    public static final String ID_COLUMN = "id";
    private static final int CONFLICT = 409;
    private static final String USER_ROLE_NAME = "user"; // роль на уровне реалма

    //private final UserService service;
    //private final UserWebClientBuilder userWebСlient;
    //private MessageFuncAction messageFuncAction;
    private final String TOPIC_NAME = "mytopic";
    private KafkaTemplate<String,Long> kafkaTemplate;
    private KeycloakUtils keycloakUtils;

    public AdminController(
 //                         UserService service,
//                          UserWebClientBuilder userWebСlient,
//                          MessageFuncAction messageProduceService,
                           KafkaTemplate<String,Long> kafkaTemplate,
                           KeycloakUtils keycloakUtils)
    {
        //this.service = service;
        //this.userWebСlient = userWebСlient;
        //this.messageFuncAction = messageProduceService;
        this.kafkaTemplate = kafkaTemplate;
        this.keycloakUtils = keycloakUtils;
    }


    @PostMapping("/add")
    public ResponseEntity add (@RequestBody UserKcDTO user) {
//        if (user.getId().trim().length() > 0)
//            return new ResponseEntity("id must be null", HttpStatus.NOT_ACCEPTABLE);
        if (user.getEmail() == null || user.getEmail().trim().length() == 0)
            return new ResponseEntity("missed email",HttpStatus.NOT_ACCEPTABLE);
        if (user.getUserpassword() == null || user.getUserpassword().trim().length() == 0)
            return new ResponseEntity("missed password",HttpStatus.NOT_ACCEPTABLE);
        if (user.getUsername() == null || user.getUsername().trim().length() == 0)
            return new ResponseEntity("missed username",HttpStatus.NOT_ACCEPTABLE);

        // добавить в бд
        // user = service.add(user);

//        if (user != null)
//            userWebСlient.initUserData(user.getId())
//                    .subscribe(aBoolean -> // через ctrl space нашел название переменной
//                            System.out.println("user populated:" + aBoolean));

//        if (user != null)
//            // rabbitmq
//            //messageFuncAction.sendNewUserMessage(user.getId());
//            // kafka:
//            kafkaTemplate.send(TOPIC_NAME,user.getId());
        Response createdResponse = keycloakUtils.createKeycloakUser(user);
        if (createdResponse.getStatus() == CONFLICT)
            return new ResponseEntity("user or email already exists: " + user.getEmail(),HttpStatus.CONFLICT);

        String userId = CreatedResponseUtil.getCreatedId(createdResponse);
        System.out.printf("User created with userId: %s%n", userId);

        //Add Roles
        List<String> defaultRoles = new ArrayList<>();
        defaultRoles.add(USER_ROLE_NAME);
        defaultRoles.add("admin");

        keycloakUtils.addRoles(userId,defaultRoles);

        return ResponseEntity.status(createdResponse.getStatus()).build();
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody UserKcDTO user) {
//        if (user.getId() == null || user.getId() == 0)
//            return new ResponseEntity("id must be not null", HttpStatus.NOT_ACCEPTABLE);
//        if (user.getEmail() == null || user.getEmail().trim().length() == 0)
//            return new ResponseEntity("missed email",HttpStatus.NOT_ACCEPTABLE);
//        if (user.getUserpassword() == null || user.getUserpassword().trim().length() == 0)
//            return new ResponseEntity("missed password",HttpStatus.NOT_ACCEPTABLE);
//        if (user.getUsername() == null || user.getUsername().trim().length() == 0)
//            return new ResponseEntity("missed username",HttpStatus.NOT_ACCEPTABLE);
        //service.update(user);

        if (user.getId().isBlank())
            return new ResponseEntity("missed param:id",HttpStatus.NOT_ACCEPTABLE);

        keycloakUtils.updateKeycloakUser(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/deletebyid")
    public ResponseEntity deleteByUserId(@RequestBody String userId) {
//        для бд
//        try {
//            service.deleteByUserId(userId);
//        } catch (EmptyResultDataAccessException e) {
//            e.printStackTrace();
//            return new ResponseEntity("user id = "+userId+" not found",HttpStatus.NOT_ACCEPTABLE);
//        }

        keycloakUtils.deleteKeycloakUser(userId);
        return new ResponseEntity(HttpStatus.OK);
    }
//    @PostMapping("deletebyemail")
//    public ResponseEntity deleteByUserEmail (@RequestBody String email) {
//        try {
//            service.deleteByUserEmail(email);
//        } catch (EmptyResultDataAccessException e) {
//            e.printStackTrace();
//            return new ResponseEntity("email = "+email+" not found",HttpStatus.NOT_ACCEPTABLE);
//        }
//        return new ResponseEntity(HttpStatus.OK);
//    }

    @PostMapping("/id")
    public ResponseEntity findById(@RequestBody String userId) {
        // для бд
//        Optional<User> userOptional = service.findById(id);
//        try {
//            if (userOptional.isPresent())
//                return ResponseEntity.ok(userOptional.get());
//        } catch (NoSuchElementException e) {
//            e.printStackTrace();
//        }
//        return new ResponseEntity("id= "+id+" not found", HttpStatus.NOT_ACCEPTABLE);

        return ResponseEntity.ok(keycloakUtils.findUserById(userId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserRepresentation>> findByEmail(@RequestBody String email) {
//        User user = null;
//        try {
//            user = service.findByEmail(email);
//        } catch (NoSuchElementException e) {
//            e.printStackTrace();
//            return new ResponseEntity("email= "+email+" not found", HttpStatus.NOT_ACCEPTABLE);
//        }
        return ResponseEntity.ok(keycloakUtils.searchKeycloakUsers("email:"+email));
    }

//    @PostMapping("/search")
//    public ResponseEntity<Page<User>> search(@RequestBody UserDTO dto) {
//        String email = dto.getEmail() != null ? dto.getEmail() : null;
//        String username = dto.getUsername() != null ? dto.getUsername() : null;
//
////        if (email == null || email.trim().length() == 0)
////            return new ResponseEntity("missed email",HttpStatus.NOT_ACCEPTABLE);
////        if (username == null || username.trim().length() == 0)
////            return new ResponseEntity("missed username",HttpStatus.NOT_ACCEPTABLE);
//
//        String sortColumn = dto.getSortColumn() != null ? dto.getSortColumn() : null;
//        String sortDirection = dto.getSortDirection() != null ? dto.getSortDirection() : null;
//
//        Integer pageNumber = dto.getPageNumber() != null ? dto.getPageNumber() : null;
//        Integer pageSize = dto.getPageSize() != null ? dto.getPageSize() : null;
//
//        Sort.Direction direction = sortDirection == null || sortDirection.trim().length() == 0
//                || sortDirection.trim().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Sort sort = Sort.by(direction,sortColumn,ID_COLUMN);
//        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize,sort);
//
//        Page<User> result = service.findByParams(email,username,pageRequest);
//
//        return ResponseEntity.ok(result);
//
//
//    }
}

