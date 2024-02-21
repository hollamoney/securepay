package com.firisbe.securepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
public class SecurepayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurepayApplication.class, args);
	}

}
