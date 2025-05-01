package angms.rs.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableScheduling
public class SpringSecurityConfig {


    // конфигурация чтобы сделать какой-либо запрос доступным
    @Value("${client.url}")
    private String clientURL;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KcRoleConverter());

        http.authorizeHttpRequests(authz -> authz // хотим поработь с запросами
//                        .requestMatchers("/admin/*").hasRole("admin")
//                        .requestMatchers("/user/*").hasRole("user") // какие доступны
                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable()) // отключить встроенную защиту от csrf т.к. исп из oauth2

                .cors(cors -> cors.configurationSource(request -> { // чтобы разрешить options запросы от клиента
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of(clientURL)); // Разрешенные источники
                    corsConfiguration.setAllowedMethods(List.of("*")); // Разрешенные методы
                    corsConfiguration.setAllowedHeaders(List.of("*")); // Разрешенные заголовки
//corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        );


        return http.build();
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHeaderNames((header)-> true);
        firewall.setAllowedHeaderValues((header)-> true);
        firewall.setAllowedParameterNames((parameter) -> true);
        return firewall;
    }
}
