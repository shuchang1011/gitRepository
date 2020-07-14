package com.microservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

//提供空参的构造函数
@NoArgsConstructor
//@AllArgsConstructor
//此注解在类上，提供类所有属性的get和set方法。同时还提供 equals,canEqual,hashCode,toString等方法
@Data
//提供链式风格访问
@Accessors(chain = true)
public class Dept implements Serializable// entity --orm--- db_table
{
    private Long deptno; // 主键
    private String dname; // 部门名称
    private String db_source;// 来自那个数据库，因为微服务架构可以一个服务对应一个数据库，同一个信息被存储到不同数据库

    public Dept(String dname) {
        super();
        this.dname = dname;
    }

}
