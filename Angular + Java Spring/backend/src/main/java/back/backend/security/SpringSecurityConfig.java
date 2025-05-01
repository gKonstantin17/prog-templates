package back.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {


    // конфигурация чтобы сделать какой-либо запрос доступным
    @Value("${client.url}")
    private String clientURL;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz // хотим поработь с запросами
                        .anyRequest().permitAll()
                )

                .csrf(csrf -> csrf.disable()) // отключить встроенную защиту от csrf т.к. исп из oauth2

                .cors(cors -> cors.configurationSource(request -> { // чтобы разрешить options запросы от клиента
                    var corsConfiguration = new CorsConfiguration();
                    // что разрешено, перечислять через запятую
                    corsConfiguration.setAllowedOrigins(List.of(clientURL)); // Разрешенные источники
                    corsConfiguration.setAllowedMethods(List.of("*")); // Разрешенные методы
                    corsConfiguration.setAllowedHeaders(List.of("*")); // Разрешенные заголовки
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }));


        return http.build();
    }

}