package com.github.acme.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SensorAlertsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorAlertsApplication.class, args);
	}
}
