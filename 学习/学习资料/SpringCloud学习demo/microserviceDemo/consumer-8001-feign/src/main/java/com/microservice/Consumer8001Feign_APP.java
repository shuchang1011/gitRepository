package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
//开启feignclient,并配置扫描com.microservice包下的feignClient
@EnableFeignClients(basePackages= {"com.microservice"})
//@ComponentScan("com.microservice")
public class Consumer8001Feign_APP {
    public static void main(String[] args) {
        SpringApplication.run(Consumer8001Feign_APP.class, args);
    }
}
