# Сервис Users

регистрирует все сервисы работающие в проекте

**Зависимости:**
такие же как у [Todo](../planner-todo/README.md)

у main метода должна быть аннотация `@EnableDiscoveryClient`

application.properties
```
spring.application.name=planner-users
spring.config.import=optional:configserver:http://localhost:8888
spring.profiles.active=users,logging,kafka,kc
```
[Профили](docs/ConfigFiles.md) из config server
который включает planner-server.properties:

Security аналогично как у [Todo](../planner-todo/docs/Security.md)

[Структура проекта](docs/Sctructure.md)

***
[RootProject](../README.md)