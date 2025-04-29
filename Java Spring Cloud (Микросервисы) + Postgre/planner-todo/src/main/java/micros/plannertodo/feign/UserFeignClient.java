package micros.plannertodo.feign;

import micros.plannerentity.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// если не модуль не видит, то добавить зависимость и ComponentScan
@FeignClient(name = "planner-users")

public interface UserFeignClient {
    @PostMapping("/user/id") // слеш в начале нужен
    ResponseEntity<User> findUserById(@RequestBody Long id);
}
