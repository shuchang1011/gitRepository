package com.microservice.controller;

import com.microservice.entity.Dept;
import com.microservice.service.DeptService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeptController {
    @Autowired
    private DeptService service = null;

    //client是获取服务注册的客户端
    @Qualifier("discoveryClient")
    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
    //一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
    @HystrixCommand(fallbackMethod = "processHystrix_Get")
    public Dept get(@PathVariable("id") Long id) {

        Dept dept = this.service.get(id);

        System.out.println(dept.getDname());
        /*if (null == dept) {
            throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
        }*/

        return dept;
    }

    @RequestMapping(value = "/dept/list", method = RequestMethod.GET)
    //一旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法
    //@HystrixCommand(fallbackMethod = "processHystrix_Get")
    public List<Dept> list() {

        List<Dept> deptList = this.service.list();

        if (deptList.size() == 0) {
            throw new RuntimeException("没有没有对应的信息");
        }

        return deptList;
    }

    public Dept processHystrix_Get(@PathVariable("id") Long id) {
        return new Dept().setDeptno(id).setDname("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand")
                .setDb_source("no this database in MySQL");
    }

    /**
     * 服务发现：可以得到当前在eureka中已注册的服务,暴露给消费端服务的一些信息
     *
     * @return
     */
    @RequestMapping(value = "/dept/discovery", method = RequestMethod.GET)
    public Object discovery() {
        //获取到注册的服务
        List<String> list = client.getServices();
        System.out.println("**********" + list);

        //通过注册的服务名获取对应的服务属性
        List<ServiceInstance> srvList = client.getInstances("MICROSERVICE-PROVIDER-8001");
        for (ServiceInstance element : srvList) {
            System.out.println(element.getServiceId() + "\t" + element.getHost() + "\t" + element.getPort() + "\t"
                    + element.getUri());
        }
        return this.client;
    }
}
