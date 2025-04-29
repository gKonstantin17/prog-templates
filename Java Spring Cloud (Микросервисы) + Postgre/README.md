# Java Spring Cloud + Postgre

JDK 23

микросервисная архитектура включает модули:

[Eureka server](planner-server/README.md) для регистрации сервисов (порт 8761)

[Config server](planner-config/README.md) для загрузки application.properties (порт 8888)

[Gateway](planner-gateway/README.md) для перенаправления запросов на сервисы (порт 8765)

сервисы:

[Todo](planner-todo/README.md)

[Users](planner-users/README.md)

Также выполняющие роль библиотек:

[[|Entity]]

[[|Utils]]

Доступ к сервисам предоставляет после авторизации Keycloak (oauth2)

**Настройки** корневого проекта

build.gradle содержит только информацию об 
```
group = 'oop'
version = '1.0-SNAPSHOT'
```

settings.gradle включает в себя внутренние модули
(settings.gradle удален из модулей)
```
rootProject.name = 'planner-micros'
include 'planner-server'
include 'planner-config'
include 'planner-gateway'
include 'planner-todo'
include 'planner-entity'
include 'planner-utils'
include 'planner-users'
```



