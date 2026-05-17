package com.itheima.fin.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 开启服务发现
public class GatewayRunApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewayRunApp.class, args);
        System.out.println("====== 大堂经理(网关) 上线，当前镇守 8080 端口！ ======");
    }
}