package micros.plannertodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"micros.plannerentity"})
@ComponentScan(basePackages = {"micros.plannerutils"})
@ComponentScan(basePackages = {"micros.plannertodo"})
@EnableFeignClients
@RefreshScope
public class PlannerTodoApplication {
	public static void main(String[] args) {

		SpringApplication.run(PlannerTodoApplication.class, args);
	}

}
