package micros.plannerutils.rest.webclient;

import micros.plannerentity.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class UserWebClientBuilder {
    private static final String baseUrlUser = "http://localhost:8765/planner-users/user/";
    private static final String baseUrlData = "http://localhost:8765/planner-todo/data/";
    public boolean userExist(Long userId) {
        try {
            // синхронный запрос
            // вызывает findById из мс plannerUser
            // который находит по пути planner-users/user/id
            User user = WebClient.create(baseUrlUser) // по какому пути контроллер
                    .post()                       // какой запрос выполнять
                    .uri("id")                // путь запроса
                    .bodyValue(userId)            // что отправлять
                    .retrieve()                   // выполнить запрос
                    .bodyToFlux(User.class)       // упаковать в User.class
                    .blockFirst();                // блокировать до получения 1й записи
                    // блок и реализует синхронность

            if (user != null)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public Flux<User> userExistAsync(Long userId) {
        Flux<User> fluxUser = WebClient.create(baseUrlUser)
                .post()
                .uri("id")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(User.class);

        return fluxUser;
    }

    public Flux<Boolean> initUserData(Long userId) {
        Flux<Boolean> fluxUser = WebClient.create(baseUrlData)
                .post()
                .uri("init")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(Boolean.class);
        return fluxUser;
    }


}
