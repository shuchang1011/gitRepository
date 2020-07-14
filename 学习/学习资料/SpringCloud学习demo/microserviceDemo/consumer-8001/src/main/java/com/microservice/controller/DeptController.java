package com.microservice.controller;

import com.microservice.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping(value = "/consumer")
public class DeptController {

    private static final String PROVIDER_PREFIX = "http://MICROSERVICE-PROVIDER";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
    public Dept get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PROVIDER_PREFIX + "/dept/get/" + id, Dept.class);
    }

    @RequestMapping(value = "/dept/list", method = RequestMethod.GET)
    //一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
    //@HystrixCommand(fallbackMethod = "processHystrix_Get")
    public List<Dept> list() {
        return restTemplate.getForObject(PROVIDER_PREFIX + "/dept/list", List.class);
    }

    @RequestMapping(value = "/dept/discovery", method = RequestMethod.GET)
    public Object discovery() {
        return restTemplate.getForObject(PROVIDER_PREFIX + "/dept/discovery", Object.class);
    }

}
