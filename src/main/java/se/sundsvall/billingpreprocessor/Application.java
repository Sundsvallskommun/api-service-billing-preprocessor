package se.sundsvall.billingpreprocessor;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@IntegrationComponentScan
@EnableIntegration
@EnableFeignClients
@EnableAsync
@EnableScheduling
public class Application {
	public static void main(String... args) {
		run(Application.class, args);
	}
}
