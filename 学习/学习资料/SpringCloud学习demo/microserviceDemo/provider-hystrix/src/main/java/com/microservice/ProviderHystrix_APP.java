package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
//开启服务发现
@EnableDiscoveryClient
//对hystrixR熔断机制的支持
@EnableCircuitBreaker
public class ProviderHystrix_APP {
    public static void main(String[] args) {
        SpringApplication.run(ProviderHystrix_APP.class, args);
    }
}
