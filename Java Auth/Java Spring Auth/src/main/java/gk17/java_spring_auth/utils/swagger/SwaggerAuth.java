package gk17.java_spring_auth.utils.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerAuth {

    @Value("${swagger.https:false}")
    private boolean useHttps;

    @Value("${swagger.host:localhost}")
    private String host;

    @Value("${swagger.external-port:443}")
    private String externalPort;
    @Value("${swagger.show-port:false}")
    private boolean showPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String protocol = useHttps ? "https" : "http";

        // Формируем URL сервера
        String serverUrl;
        if (showPort) {
            serverUrl = String.format("%s://%s:%s", protocol, host, externalPort);
        } else {
            serverUrl = String.format("%s://%s", protocol, host);
        }

        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Сервер авторизации")
                ))
                .info(new Info()
                        .title("Java Spring Auth API")
                        .description("API для аутентификации и авторизации с JWT токенами")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@example.com")
                                .url("https://security.cloudpub.ru"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Введите JWT токен. Формат: 'Bearer {токен}'"))
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("access_token")
                                .description("JWT токен в cookie 'access_token'"))
                        .addSecuritySchemes("jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT авторизация. Токен полученный через /api/auth/login")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth")
                        .addList("cookieAuth")
                        .addList("jwt"));
    }
}