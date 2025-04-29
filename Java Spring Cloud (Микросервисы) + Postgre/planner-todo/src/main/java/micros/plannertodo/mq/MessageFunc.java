package micros.plannertodo.mq;

import lombok.Getter;
import micros.plannertodo.service.TestDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;
@Configuration
@Getter
public class MessageFunc {
//    private TestDataService testDataService;
//    public MessageFunc(TestDataService testDataService) {this.testDataService = testDataService;}
//
//    @Bean
//    public Consumer<Message<Long>> newUserActionConsume() {
//        return longMessage -> testDataService.init(longMessage.getPayload());
//    }
}
