package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient

public class Consumer8001_APP {
    public static void main(String[] args) {
        SpringApplication.run(Consumer8001_APP.class, args);
    }
}
