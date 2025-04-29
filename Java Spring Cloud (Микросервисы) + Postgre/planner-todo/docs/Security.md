```
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // включить механизм для защиты методов по ролям
public class SpringSecurityConfig {
    // конфигурация чтобы сделать какой-либо запрос доступным

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // подключение конвертера
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/categoty/*","/task/*","/priority/*").hasRole("user")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();

    }
}
```
[Назад](../README.md)