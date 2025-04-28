# Java Spring приложение в связке с Keycloak (oauth2)

Реализует механизмы аутентификации и авторизации технологии ОAuth2
с помошью Spring Security

у пользователя возможны роли _user_ и _admin_

**Настройки:**

JDK 23

Зависимости Spring:
- Spring Web
- OAuth2 Resource Server

Библиотеки подключены через gradle (см файл build.gradle)

**application.properties**

указан порт и адрес для отправки запросов в keycloak

`spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/todoapp-realm/protocol/openid-connect/certs
` 

настройки реалма keycloak экспортированы в файл `realm-export.json`


## Конфигурация Security
подключается конвертер ролей из jwt токенов, конвертацию реализует класс KCRoleConverter

с помощью 

`authz.requestMatchers("/user/*").hasRole("user")`

`.requestMatchers("/admin/*").hasRole("admin")`

разрешаются запросы пользователей с ролями к соответвующим маршрутам (контроллерам)

#### можно разрешить любым пользователям

`.requestMatchers("work/login").permitAll()`

и задать доступ к методу контроллера для конкретной роли

```
@GetMapping("/view")
@PreAuthorize("hasRole('user')") // или @PreAuthorize("hasAuthority('ROLE_admin')")
public String view() {
   return "view work";
}
```

для этого нужно задать перед конфигурацией

`@EnableMethodSecurity(prePostEnabled = true)`
