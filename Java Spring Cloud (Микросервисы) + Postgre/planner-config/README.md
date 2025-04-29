# Config server
загружает конфигурацию application.properties из GitHub репозитория

**Зависимости:**
Config server

у main метода должна быть аннотация `@EnableConfigServer`

у application.properties 
```
spring.application.name=planner-config
server.port=8888

# url private github repository
spring.cloud.config.server.git.uri=https://github.com/gKonstantin17/prop-test.git

# git clone on start
#spring.cloud.config.server.git.clone-on-start=true
# where to clone remote repository
spring.cloud.config.server.git.basedir=file://c:/test/planner-cfg/prop-test

# github access
spring.cloud.config.server.git.username=gKonstantin17
spring.cloud.config.server.git.password=token

# name main branch
spring.cloud.config.server.git.default-label=main
logging.pattern.console=%C{1.} [%-5level] %d{HH:mm:ss} - %msg%n
```
ссылка на приватный репозиторий и настройки для доступа к нему

можно перечислить конфигурацию по названию сервиса
- planner-server.properties
- planner-gateway.properties

общий для всех
- application.properties

использовать профили
- application-todo.properties
- application-users.properties
- application-todo.properties
- application-micro.properties



[RootProject](../README.md)