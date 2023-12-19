package com.emanuel.offers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@RestController
@EnableScheduling
@OpenAPIDefinition(info= @Info(title="Offers API", version = "1.0", description = "Project of Offers API."))
public class OffersBciApplication {

	public static void main(String[] args) {
		SpringApplication.run(OffersBciApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "System of Offers";
	}
	
}
