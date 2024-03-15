package se.sundsvall.billingpreprocessor;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@IntegrationComponentScan
@EnableIntegration
public class Application {
	public static void main(String... args) {
		run(Application.class, args);
	}
}
