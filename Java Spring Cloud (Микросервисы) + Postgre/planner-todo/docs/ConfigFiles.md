Профили из config server

micro
```
# url, куда будет происходить регистрация микросервисов
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# автоматически порт будет присвоен случайный
server.port=0

# для того, чтобы каждый инстанс (экземпляр) имел уникальный id
eureka.instance.instance-id=${spring.application.name}:${random.uuid}

#включаем связку circuitbreaker+feign
feign.circuitbreaker.enabled=true
```

todo
```
# настройки соединения с БД
spring.datasource.url=jdbc:postgresql://localhost:5432/planner_todo
spring.datasource.username=postgres
spring.datasource.password=rootroot
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
```

logging
```
# логи обычно выводятся при разработке приложения, чтобы видеть что происходит. В боевом режиме нужно оставить только самые критичные логи (WARN, ERROR)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.type.descriptor.sql=trace

logging.level.org.springframework.security=trace

server.error.include-message=always
```


kafka
```
# настройки для подключения kafka сервера
spring.kafka.consumer.bootstrap-servers: localhost:9092
spring.kafka.consumer.group-id: jb
spring.kafka.consumer.key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

spring.kafka.producer.bootstrap-servers: localhost:9092
spring.kafka.producer.key-serializer: org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

[Назад](../README.md)