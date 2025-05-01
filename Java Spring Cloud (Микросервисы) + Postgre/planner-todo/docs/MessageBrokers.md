### Kafka
сервер установлен по порту 9092

в build.gradle зависимость

`implementation 'org.springframework.kafka:spring-kafka'`

в cfg сервер профиль
application-kafka.properties
```
spring.kafka.consumer.bootstrap-servers=localhost:9092  
spring.kafka.consumer.group-id=micr  
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer  
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer  
  
spring.kafka.producer.bootstrap-servers=localhost:9092  
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer  
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

#### Отправитель
в контроллер добавляем название топика и экземпляр шаблона (через контструктор)
```
private final String TOPIC_NAME = "mytopic";  
private KafkaTemplate<String,Long> kafkaTemplate;
public UserController(UserService service,  
KafkaTemplate<String,Long> kafkaTemplate)  
{
	this.service = service;   
	this.kafkaTemplate = kafkaTemplate;  
}
```

метод add
```
if (user != null)  
	kafkaTemplate.send(TOPIC_NAME,user.getId());  
return ResponseEntity.ok(user);
```

#### Получатель
в сервисе создаем метод с аннотацией и описываем логику
```
@KafkaListener(topics = "mytopic")  
public void listenKafka(Long userId) {  
	// выполнение логики при получении данных  
	System.out.println("new userId =" + userId);  
	//init(userId);  // метод который создает связанные записи
}
```
### RabbitMq
сервер установлен по порту 5672

в build.gradle зависимость
`implementation 'org.springframework.cloud:spring-cloud-starter-stream-rabbit'`

в cfg сервер отправляем
application-rabbit.properties
```
spring.rabbitmq.host=localhost  
spring.rabbitmq.port=5672  
spring.rabbitmq.username=test  
spring.rabbitmq.password=test
spring.rabbitmq.virtual-host=/

# логи
logging.level.org.springframework.cloud.stream.messaging=trace
```
Создание канала в который будут попадать все ошибочные сообщения
в приложение куда приходят сообщения в cfg server
`spring.cloud.stream.rabbit.bindings.todoInputChannel.consumer.auto-bind-dlq=true`
создается автоматически (желательно удалять очереди из rabbitmq, если не создается)


newUserActionProduce - название метода
методы перечисляются через ;
`spring.cloud.function.definition=newUserActionProduce`


spring.cloud.stream.bindings.newUserActionProduce-out-0.destination=planner-dest
`newUserActionProduce-out-0` -> out - отправлять, 0 - первый тип параметров метода
planner-dest  - название канала (должно совпадать у отправителя/получателя )


### <span style="color:rgb(146, 208, 80)">Отправитель</span>
создаем шину (очередь), которая будет хранить сообщения
объявляем метод, который взаимодействовать с шиной
лепим @Bean чтобы его можно было найти в настройках
Supplier - output
```
// сам канал
@Configuration  
@Getter  
public class MessageFunc {  
	// шина из которой отправляются сообщения  
	private Sinks.Many<Message<Long>> innerBus = Sinks.many().multicast()  
				.onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE,false);  
	  
	// подписаться на эту шину  
	// как только положат, supplier отправит  
	// название метода должно совпадать с definition и bindings  
	@Bean  
	public Supplier<Flux<Message<Long>>> newUserActionProduce() {  
		return () -> innerBus.asFlux();  
	}  
}
```

делаем сервис, который через канал будет оправлять сообщение
```
// работа с каналами
@Service  
@Getter  
public class MessageFuncAction {  
	// каналы для обмена сообщениями  
	private MessageFunc messageFunc;  
	public MessageFuncAction(MessageFunc streamFunction)  
	{  
		this.messageFunc = streamFunction;  
	}  
	// отправка сообщений  
	public void sendNewUserMessage(Long id) {  
		messageFunc.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);  
		System.out.println("message send:" + id);  
	}  
}
```
в контроллере вызываем метод из сервиса
```
if (user != null)  
	messageFuncAction.sendNewUserMessage(user.getId());  
  
return ResponseEntity.ok(user);
```

application.properties
в 1 строке:
объявляем название метода, который создали в классе с настройками канала
можно перечислить через ;

в 2 строке:
через метод указываем тип канала (out) и номер параметра (0) из метода который будет отправляться
planner-dest - название канала
```
spring.cloud.function.definition=newUserActionProduce
spring.cloud.stream.bindings.newUserActionProduce-out-0.destination=planner-dest
```

### <span style="color:rgb(146, 208, 80)">Получатель</span>
объявляем метод, который получит сообщение и реализует логику -> передаст его в метод из сервиса.
лепим @Bean чтобы его можно было найти в настройках
Consumer - input
```
@Configuration  
@Getter  
public class MessageFunc {  
	private TestDataService testDataService;  
	public MessageFunc(TestDataService testDataService) {
		this.testDataService = testDataService;
	}  
	  
	@Bean  
	public Consumer<Message<Long>> newUserActionConsume() {  
		return longMessage -> testDataService.init(longMessage.getPayload());  
	}  
}
```

application.properties
в 1 строке:
объявляем название метода, который создали в классе с настройками канала

в 2 строке:
через метод указываем тип канала (in) и номер параметра (0) из метода который будет получать
planner-dest - название канала <span style="color:rgb(255, 255, 0)">должно совпадать</span> с отправителем
в 3 строке: метод добавляется в группу чтобы разные слушатели могли слушать 1 канал
```
spring.cloud.function.definition=newUserActionConsume
spring.cloud.stream.bindings.newUserActionConsume-in-0.destination=planner-dest
spring.cloud.stream.bindings.newUserActionConsume-in-0.group=planner-group
```

[Назад](../README.md)