package ru.pricehunt.pleerru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaAuditing
public class SpringPricehuntPleerruApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPricehuntPleerruApplication.class, args);
	}

}
