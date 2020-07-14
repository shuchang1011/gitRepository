package com.microservice.service;

import com.microservice.entity.Dept;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/*通过feign来以声明式的方法定义服务绑定接口*/
@FeignClient(value = "MICROSERVICE-PROVIDER", fallbackFactory = DeptClientFallbackFactory.class)
public interface DeptClientService
{
    //这里的映射要对应服务提供方的接口地址，来实现服务的调用，
    // 消费端将注册中心获的服务名作为前缀，调用对应的服务时，就可以通过这个service的方法直接调用，不再需要restTemplate
    @RequestMapping(value = "/dept/get/{id}",method = RequestMethod.GET)
    public Dept getById(@PathVariable("id") long id);

    @RequestMapping(value = "/dept/list",method = RequestMethod.GET)
    public List<Dept> list();

    @RequestMapping(value = "/dept/add",method = RequestMethod.POST)
    public boolean add(Dept dept);
}
