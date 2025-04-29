package micros.plannerusers.mq;

import lombok.Getter;
import org.springframework.stereotype.Service;



//  работа с каналами
@Service
@Getter
public class MessageFuncAction {
//    // каналы для обмена сообщениями
//    private MessageFunc messageFunc;
//    public MessageFuncAction(MessageFunc streamFunction)
//    {
//        this.messageFunc = streamFunction;
//    }
//    // отправка сообщений
//    public void sendNewUserMessage(Long id) {
//        messageFunc.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);
//        System.out.println("message send:" + id);
//    }
}
