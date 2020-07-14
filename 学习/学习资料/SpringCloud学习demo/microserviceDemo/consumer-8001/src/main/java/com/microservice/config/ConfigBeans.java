package com.microservice.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBeans {

    @Bean
    //这里不使用这个注解的话，restTemplate会依照rest风格解析，并不能识别服务名,同时，他还开启了负载均衡策略，支持客户端的负载均衡
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public IRule  myRule() {
        return new RandomRule();
    }

}
