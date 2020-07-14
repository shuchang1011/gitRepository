package com.microservice.service;

import com.microservice.entity.Dept;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/*实现FallbackFactory，并传入对应的Service接口，结合Feign来实现接口下所有方法异常时的集中处理，降低系统的耦合*/
/*服务降级是当因为服务器压力而暂停服务时，消费方访问对应宕机的服务时，能够快速返回，不至于不可用，作用在服务消费方
服务熔断则是对某些异常和超时的快速响应处理，作用在服务提供方，避免服务因为异常或超时，导致的链式阻塞*/
@Component
public class DeptClientFallbackFactory implements FallbackFactory<DeptClientService> {
    @Override
    public DeptClientService create(Throwable throwable) {
        return new DeptClientService() {
            @Override
            public Dept getById(long id) {
                return new Dept().setDeptno(id)
                        .setDname("该ID："+id+"没有没有对应的信息,Consumer客户端提供的降级信息,此刻服务Provider已经关闭")
                        .setDb_source("no this database in MySQL");
            }

            @Override
            public List<Dept> list() {
                return null;
            }

            @Override
            public boolean add(Dept dept) {
                return false;
            }
        };
    }
}
