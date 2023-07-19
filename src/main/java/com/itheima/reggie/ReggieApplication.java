package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication //启动类注解
@ServletComponentScan //扫描Servlet组件
@EnableTransactionManagement //开启事务管理
@EnableCaching //开启缓存
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
        log.info("后台管理系统默认链接: http://localhost:8080/backend/index.html");
        log.info("前台管理系统默认链接: http://localhost:8080/front/index.html");
    }
}
