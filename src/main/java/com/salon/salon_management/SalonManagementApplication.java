package com.salon.salon_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.salon.salon_management.entity")
@EnableJpaRepositories(basePackages = "com.salon.salon_management.repository")
public class SalonManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalonManagementApplication.class, args);
	}

}
