package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 配置mybatis-plus提供的分页插件拦截器
// @Configuration 注解用于标记一个类为配置类，告诉 Spring 容器该类是用于定义 Bean 的配置类。
@Configuration
public class MybatisPlusConfig {
    // @Bean 注解用于在 Spring 中声明一个 Bean 对象。当 Spring 容器启动时，会自动扫描配置类中的 @Bean 注解，
    // 并根据其配置创建相应的 Bean 实例，并将其纳入 Spring 容器的管理。
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
