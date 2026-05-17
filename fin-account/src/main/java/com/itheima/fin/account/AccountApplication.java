package com.itheima.fin.account;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //开启 nacos 注册服务发现
public class AccountApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AccountApplication.class, args);
        System.out.println("====== 账户微服务(fin-account) 启动成功！======");
    }
}
