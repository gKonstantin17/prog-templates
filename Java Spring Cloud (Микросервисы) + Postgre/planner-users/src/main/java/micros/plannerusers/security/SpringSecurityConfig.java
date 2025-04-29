package micros.plannerusers.security;

import micros.plannerutils.converter.KCRoleConverter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

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
                        .requestMatchers("/admin/*").hasRole("admin")
                        .requestMatchers("/auth/*").hasRole("user")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();

    }
}
