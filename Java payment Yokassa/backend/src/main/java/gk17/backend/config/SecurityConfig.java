package gk17.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz // хотим поработь с запросами
                        .anyRequest().permitAll()
                )

                .csrf(csrf -> csrf.disable()) // отключить встроенную защиту от csrf т.к. исп из oauth2

                .cors(cors -> cors.configurationSource(request -> { // чтобы разрешить options запросы от клиента
                    var corsConfiguration = new CorsConfiguration();
                    // что разрешено, перечислять через запятую
                    corsConfiguration.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "https://wew.cloudpub.ru",
                            "https://api.yookassa.ru/v3/webhooks"
                            )); // Разрешенные источники
                    corsConfiguration.setAllowedMethods(List.of("*")); // Разрешенные методы
                    corsConfiguration.setAllowedHeaders(List.of("*")); // Разрешенные заголовки
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setMaxAge(3600L); // Кэширование preflight запроса

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    return corsConfiguration;
                }));


        return http.build();
    }
}