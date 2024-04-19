package com.lb.brandingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lb.brandingApp"})
public class BrandingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrandingApplication.class, args);
	}

}
