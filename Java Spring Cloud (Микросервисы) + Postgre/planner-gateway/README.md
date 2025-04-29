# Gateway
Шлюз, перенаправляет запросы на сервисы

для запроса будет путь url-адрес 

`gateway/название-сервиса`

`http://localhost:8765/planner-todo`

**Зависимости:**
Config client, Spring Boot Actuator,  Gateway, Spring Reactive Web, Eureka Discovery Client

у main метода должна быть аннотация `@EnableConfigServer`

у application.properties 
```
spring.application.name=planner-gateway
spring.config.import=optional:configserver:http://localhost:8888
server.ssl.enabled=false
```

в конфиг сервере planner-gateway
```
# url, куда будет происходить регистрация микросервисов
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# для корректной работы api gateway
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```



[RootProject](../README.md)