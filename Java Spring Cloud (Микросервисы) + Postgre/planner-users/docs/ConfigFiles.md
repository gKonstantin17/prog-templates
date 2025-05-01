users
```
# настройки соединения с БД
spring.datasource.url=jdbc:postgresql://localhost:5432/planner_users
spring.datasource.username=postgres
spring.datasource.password=rootroot
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
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
kc
```
keycloak.auth-server-url=http://localhost:8180/
keycloak.resource=user-manage-client
keycloak.realm=todoapp-realm
keycloak.ssl-required=external
keycloak.credentials.secret=vZp19WO7Fpqqwisb81ihHKmVJ3YOMPwL
keycloak.use-resource-role-mappings=true

# ссылка на сертификаты authserver для проверки целостности access token
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/todoapp-realm/protocol/openid-connect/certs
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

[Назад](../README.md)