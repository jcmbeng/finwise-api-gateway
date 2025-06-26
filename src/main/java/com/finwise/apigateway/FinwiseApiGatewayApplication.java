package com.finwise.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FinwiseApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinwiseApiGatewayApplication.class, args);
	}

}
