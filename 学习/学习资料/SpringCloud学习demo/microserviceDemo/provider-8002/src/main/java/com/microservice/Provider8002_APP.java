package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Provider8002_APP {
    public static void main(String[] args) {
        SpringApplication.run(Provider8002_APP.class, args);
    }
}
