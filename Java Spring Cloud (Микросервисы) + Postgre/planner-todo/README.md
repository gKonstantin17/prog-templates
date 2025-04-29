# Сервис Todo
RestFul подключенный к базе данных

Взаимодействет с [Users](../planner-users/README.md) по Kafka

**Зависимости:**
Config client, Spring Boot Actuator, Spring Web, Eureka Discovery Client

у main метода должна быть аннотация `@EnableDiscoveryClient`

у application.properties ссылка на сервер конфигурации (в котором находятся все другие app.props)

```
spring.application.name=planner-todo
spring.config.import=optional:configserver:http://localhost:8888
spring.profiles.active=micro,todo,logging,kafka
```

[Профили](docs/ConfigFiles.md) из config server

[Настройки Security](docs/Security.md)

[Структура проекта](docs/Sctructure.md)

[Kafka](docs/Kafka.md)

***

[RootProject](../README.md)