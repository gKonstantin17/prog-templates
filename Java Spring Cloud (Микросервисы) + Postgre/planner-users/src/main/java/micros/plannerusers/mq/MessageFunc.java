package micros.plannerusers.mq;

import lombok.Getter;

import org.springframework.context.annotation.Configuration;


// сами каналы
@Configuration
@Getter
public class MessageFunc {
//    // шина из которой отправляются сообщения
//    private Sinks.Many<Message<Long>> innerBus = Sinks.many().multicast()
//            .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE,false);
//
//    // подписаться на эту шину
//    // как только положат, supplier отправит
//    // название метода должно совпадать с definition и bindings
//    @Bean
//    public Supplier<Flux<Message<Long>>> newUserActionProduce() {
//        return () -> innerBus.asFlux();
//    }
}
