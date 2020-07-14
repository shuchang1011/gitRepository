package com.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class Configserver_APP {
    public static void main(String[] args) {
        SpringApplication.run(Configserver_APP.class, args);
    }
}
