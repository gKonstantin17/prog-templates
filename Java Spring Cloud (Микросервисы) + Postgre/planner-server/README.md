# Eureka server

регистрирует все сервисы работающие в проекте

**Зависимости:**
Config client, Eurika server, Spring Boot Actuator

у main метода должна быть аннотация `@EnableEurekaServer`

у application.properties ссылка на сервер конфигурации (в котором находятся все другие app.props)

```
spring.application.name=planner-server
server.port=8761
spring.config.import=optional:configserver:http://localhost:8888
```

который включает planner-server.properties:

```
# url, куда будет происходить регистрация микросервисов
eureka.client.service-url.defaultZone=http://localhost:8781/eureka

# отключение лишних логов, т.к. не тут не будем создавать и вызывать микросервисы
logging.level.com.netflix.eureka=OFF
logging.level.com.netflix.discovery=OFF
```

[RootProject](../README.md)