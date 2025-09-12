package com.specsShope.specsBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class SpecsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpecsBackendApplication.class, args);
	}

}
