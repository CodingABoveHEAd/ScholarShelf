package com.niloy.scholarshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ScholarShelfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScholarShelfApplication.class, args);
	}

}
