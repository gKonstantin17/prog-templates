package com.example.testauth2.security;

import com.example.testauth2.converter.KCRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // включить механизм для защиты методов по ролям
public class SpringSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // подключение конвертера
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KCRoleConverter());

//        http.authorizeHttpRequests(authz -> authz // хотим поработь с запросами
//                        .requestMatchers("work/login").permitAll() // какие доступны
//                        .anyRequest().authenticated() // остальные доступны только аутитенфицированым
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/user/*").hasRole("user")
                        .requestMatchers("/admin/*").hasRole("admin")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2 // добавить конвертер, чтобы понимать роли
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();

    }
}
