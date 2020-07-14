package com.microservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class zuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx=RequestContext.getCurrentContext();//得到request上下文对象，里面包含了此次访问的所有环境、参数支持，相当于一个工厂
        HttpServletRequest request=ctx.getRequest();//获取request
        Object accessToken=request.getParameter("token");//获取token参数，一般跨网段的项目中，我们经常采用token共享的方式来实现单点登录功能
        if(null==accessToken){
            System.out.println("token is null");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try{
                ctx.getResponse().getWriter().write("token is empty"); //当登录被拦截时返回页面的消息
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        System.out.println("登陆成功！");
        return null;
    }
}
