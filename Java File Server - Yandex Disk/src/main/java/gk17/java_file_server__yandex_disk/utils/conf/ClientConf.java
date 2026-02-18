package gk17.java_file_server__yandex_disk.utils.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConf {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
